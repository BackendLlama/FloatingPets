package net.llamasoftware.spigot.floatingpets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.command.BaseCommandExecutor;
import net.llamasoftware.spigot.floatingpets.command.Command;
import net.llamasoftware.spigot.floatingpets.command.subcommand.*;
import net.llamasoftware.spigot.floatingpets.external.packet.SteerPacketListener;
import net.llamasoftware.spigot.floatingpets.external.placeholder.PetPlaceholderExpansion;
import net.llamasoftware.spigot.floatingpets.external.wg.WGManager;
import net.llamasoftware.spigot.floatingpets.helper.NMSHelper;
import net.llamasoftware.spigot.floatingpets.helper.RegistrationHelper;
import net.llamasoftware.spigot.floatingpets.listener.EntityListener;
import net.llamasoftware.spigot.floatingpets.listener.MenuListener;
import net.llamasoftware.spigot.floatingpets.listener.PlayerListener;
import net.llamasoftware.spigot.floatingpets.listener.VehicleListener;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.manager.command.CommandManager;
import net.llamasoftware.spigot.floatingpets.manager.config.SettingManager;
import net.llamasoftware.spigot.floatingpets.manager.config.YAMLManager;
import net.llamasoftware.spigot.floatingpets.manager.cooldown.CooldownManager;
import net.llamasoftware.spigot.floatingpets.manager.menu.MenuManager;
import net.llamasoftware.spigot.floatingpets.manager.metrics.DagaMetrics;
import net.llamasoftware.spigot.floatingpets.manager.pet.PetManager;
import net.llamasoftware.spigot.floatingpets.manager.sql.MySQLManager;
import net.llamasoftware.spigot.floatingpets.manager.storage.StorageManager;
import net.llamasoftware.spigot.floatingpets.manager.storage.impl.FlatfileStorageManager;
import net.llamasoftware.spigot.floatingpets.manager.storage.impl.SQLStorageManager;
import net.llamasoftware.spigot.floatingpets.model.config.ConfigDefinition;
import net.llamasoftware.spigot.floatingpets.model.config.YAMLFile;
import net.llamasoftware.spigot.floatingpets.util.Utility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

public final class FloatingPets extends JavaPlugin {

    @Getter private final CommandManager commandManager;
    @Getter private StorageManager storageManager;
    @Getter private MySQLManager mySqlManager;
    @Getter private Locale locale;

    @Getter private final YAMLManager yamlManager;
    @Getter private final PetManager petManager;
    @Getter private final SettingManager settingManager;
    @Getter private final CooldownManager cooldownManager;

    @Getter private final Gson gson;
    @Getter private final Utility utility;
    @Getter private final NMSHelper nmsHelper;
    @Getter private final RegistrationHelper registrationHelper;
    @Getter private final MenuManager menuManager;
    @Getter private ConfigDefinition configDefinition;
    @Getter private Economy economy;

    @Getter
    private YAMLFile defaultLocaleFile;

    @Getter
    private WGManager wgManager;

    private final BaseCommandExecutor defaultExecutor;

    public FloatingPets(){
        commandManager     = new CommandManager(this);
        yamlManager        = new YAMLManager(this);
        nmsHelper          = new NMSHelper();
        settingManager     = new SettingManager(this);
        cooldownManager    = new CooldownManager();
        registrationHelper = new RegistrationHelper(this);
        utility            = new Utility(this);
        petManager         = new PetManager(this);
        defaultExecutor    = new BaseCommandExecutor(this);
        menuManager        = new MenuManager(this);
        gson               = new GsonBuilder().create();
    }

    @Override
    public void onEnable() {
        long profileStart = System.currentTimeMillis();

        getConfig().options().copyDefaults(true);
        saveConfig();

        if(isSetting(Setting.METRICS)){
            try {
                new DagaMetrics(getDescription()
                        .getVersion()).report();
            } catch (IOException ignored) { }
        }

        registerListeners();

        configDefinition = new ConfigDefinition(this, getConfig());

        if(isSetting(Setting.PET_RIDING)) {
            Plugin protocolLibPlugin = getServer().getPluginManager().getPlugin("ProtocolLib");
            if(protocolLibPlugin == null) {
                getLogger().warning("FloatingPets has been disabled " +
                        "because 'riding' option is enabled and ProtocolLib is not installed.");
                getPluginLoader().disablePlugin(this);
                return;
            }

            new SteerPacketListener(this).listen();
        }

        nmsHelper.registerCustomPet();
        nmsHelper.killPets();
        enableStorage();

        locale = new Locale(this);

        registerCommands();
        hookExternal();

        sendInfoMessage(String.format("FloatingPets v%s successfully loaded (%d ms).",
                getDescription().getVersion(), System.currentTimeMillis() - profileStart));

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::spawnStoredPets, 5L);

    }

    private void enableStorage() {
        sendInfoMessage("Enabling storage");

        defaultLocaleFile = yamlManager.loadIfNotExists("locale.yml");

        if(getStringSetting(Setting.GENERAL_STORAGE_TYPE)
                .equalsIgnoreCase(Constants.STORAGE_TYPE_MYSQL)){

            mySqlManager = new MySQLManager(
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_SERVER),
                    Integer.parseInt(getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PORT)),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_DATABASE),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_USERNAME),
                    getStringSetting(Setting.GENERAL_STORAGE_MYSQL_PASSWORD),
                    Integer.parseInt(getStringSetting(Setting.GENERAL_STORAGE_MYSQL_MAXIMUM_POOLS)),
                    getLogger());

            storageManager = new SQLStorageManager(this, mySqlManager);

        } else if (getStringSetting(Setting.GENERAL_STORAGE_TYPE)
                .equalsIgnoreCase(Constants.STORAGE_TYPE_FLATFILE)){

            storageManager = new FlatfileStorageManager(this);
        }

        storageManager.load();
    }

    @Override
    public void onDisable() {
        nmsHelper.getNmsManager().killPets();
        petManager.despawnPets();
    }

    private void registerListeners() {
        sendInfoMessage("Registering listeners");
        registerListener(new EntityListener(this));
        registerListener(new PlayerListener(this));
        registerListener(new VehicleListener());
        registerListener(new MenuListener(this));
    }

    private void registerCommands(){
        sendInfoMessage("Registering commands");

        PluginCommand baseCommand = getCommand("pet");
        if(baseCommand == null){
            getLogger().severe("Base command is invalidly (null) presented in plugin.yml");
            return;
        }

        baseCommand.setExecutor(defaultExecutor);

        Arrays.asList(
                new CommandHelp(this),
                new CommandSelect(this),
                new CommandList(this),
                new CommandSpawn(this),
                new CommandRemove(this),
                new CommandHide(this),
                new CommandTeleport(this),
                new CommandLight(this),
                new CommandCalloff(this),
                new CommandRemoveAll(this),
                new CommandAdmin(this),
                new CommandReload(this),
                new CommandParticle(this)).forEach(commandManager::registerCommand);

        registerCommand(new CommandName(this), Setting.PET_NAME_CUSTOM);
        registerCommand(new CommandRide(this), Setting.PET_RIDING);
        registerCommand(new CommandHat(this), Setting.PET_HAT_COSMETIC);
        registerCommand(new CommandSkill(this), Setting.PET_SKILLS);
    }

    private void registerCommand(Command command, Setting settingCondition){
        if(isSetting(settingCondition))
            commandManager.registerCommand(command);
    }

    private void sendInfoMessage(String message){
        getLogger().info(Constants.INFO_MESSAGE_PREFIX + message);
    }

    private void spawnStoredPets() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            List<Pet> pets = getStorageManager().getPetsByOwner(player.getUniqueId());
            pets.forEach(pet -> petManager.spawnPet(pet,
                    player.getLocation(), player, true));
        });
    }

    public Map<Setting, String> getSettingsMap(){
        Map<Setting, String> settingMap = new HashMap<>();
        Arrays.stream(Setting.values())
                .forEach(setting -> settingMap.put(setting, getStringSetting(setting)));

        return settingMap;
    }

    public boolean isPreload(StorageManager.Type type){
        return isSetting("storage.options.preload." + type.name().toLowerCase());
    }

    private void registerListener(Listener listener){
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public boolean isSetting(Setting setting){
        return isSetting(setting.getKey());
    }

    public boolean isSetting(String key){
        Optional<Setting> setting = Setting.getSettingByKey(key);

        if(setting.isPresent()) {
            if (setting.get() == Setting.PET_HEALING) {
                if(!isSetting(Setting.PET_HEALTH)){
                    return false;
                }
            }
        }

        return getSetting(key);
    }

    public void hookExternal(){

        PluginManager pluginManager = getServer().getPluginManager();

        if(pluginManager.getPlugin("WorldGuard") != null){
            wgManager = new WGManager(this);
        }

        if(pluginManager.getPlugin("PlaceholderAPI") != null){
            new PetPlaceholderExpansion(this).register();
        }

        if(pluginManager.getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }

    }

    public boolean isPet(LivingEntity entity){
        return entity.hasMetadata(Constants.METADATA_PET)
                || entity.hasMetadata(Constants.METADATA_NAME_TAG);
    }

    public Boolean getSetting(String key){ return getConfig().getBoolean("settings." + key); }

    public String getStringSetting(Setting setting){ return getConfig().getString("settings." + setting.getKey()); }

    public boolean isEconomy() {
        return economy != null;
    }

}
package gq.zunarmc.spigot.floatingpets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gq.zunarmc.spigot.floatingpets.command.BaseCommandExecutor;
import gq.zunarmc.spigot.floatingpets.command.subcommand.*;
import gq.zunarmc.spigot.floatingpets.external.placeholder.PetPlaceholderExpansion;
import gq.zunarmc.spigot.floatingpets.external.wg.WGManager;
import gq.zunarmc.spigot.floatingpets.helper.NMSHelper;
import gq.zunarmc.spigot.floatingpets.helper.RegistrationHelper;
import gq.zunarmc.spigot.floatingpets.listener.*;
import gq.zunarmc.spigot.floatingpets.locale.Locale;
import gq.zunarmc.spigot.floatingpets.manager.command.CommandManager;
import gq.zunarmc.spigot.floatingpets.manager.config.YAMLManager;
import gq.zunarmc.spigot.floatingpets.manager.menu.MenuManager;
import gq.zunarmc.spigot.floatingpets.manager.metrics.DagaMetrics;
import gq.zunarmc.spigot.floatingpets.manager.pet.PetManager;
import gq.zunarmc.spigot.floatingpets.manager.sql.MySQLManager;
import gq.zunarmc.spigot.floatingpets.manager.storage.StorageManager;
import gq.zunarmc.spigot.floatingpets.manager.storage.impl.FlatfileStorageManager;
import gq.zunarmc.spigot.floatingpets.manager.storage.impl.SQLStorageManager;
import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import gq.zunarmc.spigot.floatingpets.model.config.ConfigDefinition;
import gq.zunarmc.spigot.floatingpets.external.packet.SteerPacketListener;
import gq.zunarmc.spigot.floatingpets.model.config.YAMLFile;
import gq.zunarmc.spigot.floatingpets.model.misc.ParticleInfo;
import gq.zunarmc.spigot.floatingpets.util.Utility;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class FloatingPets extends JavaPlugin {

    @Getter private final CommandManager commandManager;
    @Getter private StorageManager storageManager;
    @Getter private MySQLManager mySqlManager;
    @Getter private Locale locale;

    @Getter private final YAMLManager yamlManager;
    @Getter private final PetManager petManager;

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
        registrationHelper = new RegistrationHelper(this);
        utility            = new Utility(this);
        nmsHelper          = new NMSHelper();
        petManager         = new PetManager(this);
        defaultExecutor    = new BaseCommandExecutor(this);
        menuManager        = new MenuManager();
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

        Arrays.stream(StorageManager.Type.values())
                .filter(this::isPreload)
                .forEach(t -> storageManager.preload(t));
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
                new CommandName(this),
                new CommandRemove(this),
                new CommandHide(this),
                new CommandRide(this),
                new CommandHat(this),
                new CommandTeleport(this),
                new CommandLight(this),
                new CommandCalloff(this),
                new CommandRemoveAll(this),
                new CommandParticle(this)).forEach(commandManager::registerCommand);
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

    private boolean isPreload(StorageManager.Type type){
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

        if(isSetting(Setting.PET_SHOP_ENABLED)) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return;
            }

            economy = rsp.getProvider();
        }

    }

    public List<ParticleInfo> getEnabledParticles(){

        ConfigurationSection section = getConfig().getConfigurationSection("settings.pet.particle.display");
        if(section == null)
            return new ArrayList<>();

        String filter = getStringSetting(Setting.PET_PARTICLE_FILTER);
        List<org.bukkit.Particle> particles;

        if(!filter.equalsIgnoreCase("none")){
            if(!(filter.equalsIgnoreCase("exclude") || filter.equalsIgnoreCase("include"))){
                getLogger().warning("Invalid particle filter specified in config.");
            }

            if(filter.equalsIgnoreCase("exclude")){
                List<String> excludedParticles = getConfig().getStringList("settings.pet.particle.filter.exclude");
                particles = Arrays.stream(org.bukkit.Particle.values()).filter(particle ->
                        !excludedParticles.contains(particle.name())).collect(Collectors.toList());
            } else {
                List<String> includedParticles = getConfig().getStringList("settings.pet.particle.filter.include");
                particles = Arrays.stream(org.bukkit.Particle.values()).filter(particle ->
                        includedParticles.contains(particle.name())).collect(Collectors.toList());
            }

        } else {
            particles = Arrays.stream(org.bukkit.Particle.values()).collect(Collectors.toList());
        }

        List<ParticleInfo> particleInfos = new ArrayList<>();

        ConfigurationSection displaySection = getConfig().getConfigurationSection("settings.pet.particle.display");
        if(displaySection == null)
            return new ArrayList<>();

        for(org.bukkit.Particle part : particles) {
            String materialStr = displaySection.getString(part.name().toLowerCase());
            particleInfos.add(ParticleInfo.builder().particle(part).material(materialStr == null ?
                    Material.REDSTONE : Material.getMaterial(materialStr)).build());
        }

        return particleInfos;
    }

    public boolean isPet(LivingEntity entity){
        return entity.hasMetadata(Constants.METADATA_PET)
                || entity.hasMetadata(Constants.METADATA_NAME_TAG);
    }

    public Boolean getSetting(String key){ return getConfig().getBoolean("settings." + key); }

    public String getStringSetting(Setting setting){ return getConfig().getString("settings." + setting.getKey()); }

}
package net.llamasoftware.spigot.floatingpets.util;

import net.llamasoftware.spigot.floatingpets.Constants;
import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetType;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.llamasoftware.spigot.floatingpets.api.model.Skill;
import net.llamasoftware.spigot.floatingpets.locale.Locale;
import net.llamasoftware.spigot.floatingpets.menu.MenuPetSpecification;
import net.llamasoftware.spigot.floatingpets.model.misc.SkillCategory;
import net.llamasoftware.spigot.floatingpets.model.skill.AttributeSkill;
import net.llamasoftware.spigot.floatingpets.model.skill.BeaconSkill;
import net.llamasoftware.spigot.floatingpets.model.skill.StorageSkill;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Optional;

public final class Utility {

    private final FloatingPets plugin;

    public Utility(FloatingPets plugin){
        this.plugin = plugin;
    }

    public String formatTitle(Pet pet, boolean color){
        if(pet == null || pet.getEntity() == null)
            return "";

        String format = plugin.getStringSetting(Setting.PET_NAME_FORMAT);

        format = plugin.getLocale().transformPlaceholders(format,
                new Locale.Placeholder("health", Constants.DEFAULT_DECIMAL_FORMAT.format(pet.getEntity().getEntityHealth())));

        // format = format.replace("{health}", new DecimalFormat("#.#").format(pet.getEntity().getEntityHealth()));
        format = plugin.getLocale().color(format);
        // format = format.replace("{name}", pet.getName());
        format = plugin.getLocale().transformPlaceholders(format,
                new Locale.Placeholder("name", pet.getName()));

        return color ? plugin.getLocale().color(format) : format;
    }

    public String formatPrice(double price){
        // TODO Use Setting enum
        boolean shop = plugin.isSetting(Setting.PET_SHOP_ENABLED);
        return shop ?
                price > 0 ?
                    plugin.getLocale().transformPlaceholders(plugin.getStringSetting(Setting.PET_SHOP_FORMAT_TEXT),
                    new Locale.Placeholder("price",
                            new DecimalFormat(plugin.getStringSetting(Setting.PET_SHOP_FORMAT_DECIMAL)).format(price)),
                    new Locale.Placeholder("currency_symbol",
                            plugin.getStringSetting(Setting.PET_SHOP_FORMAT_CURRENCY))) :
                plugin.getStringSetting(Setting.PET_SHOP_FORMAT_FREE) :
                plugin.getStringSetting(Setting.PET_SHOP_FORMAT_FREE);
    }

    public Integer[] parsePetId(Player player, String command, String[] arguments, boolean spawned, boolean... longer){
        boolean ln = (longer != null && longer.length > 0) && longer[0];
        LinkedList<Pet> pets = spawned ?
                plugin.getPetManager().getPetsByOwner(player) :
                plugin.getStorageManager().getPetsByOwner(player.getUniqueId());

        if(arguments.length != 0 && !ln){
            if (!Constants.INTEGER_PATTERN.matcher(arguments[0]).matches())
                return parsePetId(player, command, arguments, spawned, true);

            return new Integer[]{Integer.parseInt(arguments[0]), 0};
        } else {
            if(pets.size() != 1){
                MenuPetSpecification menu = new MenuPetSpecification(plugin.getStorageManager()
                        .getLocaleByKey("menus.specification.title"), pets);

                menu.setData("coloredNames", player.hasPermission("floatingpets.name.color"));
                menu.setData("command", Constants.PET_COMMAND_NAME + " " + command);
                menu.setData("args", arguments);

                plugin.getMenuManager().openMenu(player, menu, plugin);
                return null;
            }
        }

        return new Integer[]{0};
    }

    public ItemBuilder getPetDisplayItem(PetType type){
        ItemBuilder petItemBuilder = new ItemBuilder(plugin.getNmsHelper()
                .getItemStackFromTexture(type.getTexture()))
                .name(plugin.getLocale().getText("selector.name",
                        new Locale.Placeholder("type", type.getName())));

        if(plugin.isSetting(Setting.PET_SHOP_ENABLED)) {
            petItemBuilder.lore(formatPrice(type.getPrice()));
        }

        return petItemBuilder;
    }

    public int getDistanceFromGround(Entity entity) {
        World world = entity.getWorld();

        Location loc = entity.getLocation().clone();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int distance = loc.getBlockY();

        for(;distance>0;distance--){
            Block bl = world.getBlockAt(x, distance, z);
            if(bl.getType().isSolid()){
                break;
            }
        }

        return loc.getBlockY() - distance;
    }

    public long getPermissionBasedSetting(Player player, String sectionKey, String node, long opValue){
        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("settings." + sectionKey);

        if(section == null)
            return 0;

        if(player.isOp())
            return opValue;

        long limit = 0;
        if(section.contains("default")){
            limit = section.getLong("default");
        }

        for(String key : section.getKeys(false)){
            if(player.hasPermission("floatingpets." + node + "." + key)){
                long val = section.getLong(key);
                if(val > limit){
                    limit = val;
                }
            }
        }

        return limit;
    }

    public static String serializeSkill(Skill skill){
        Skill.Type type = skill.getType();
        int level       = skill.getLevel();

        return type + ":" + level;
    }

    public static Skill deserializeSkill(String serialized, FloatingPets plugin){
        String[] data = serialized.split(":");
        Skill.Type type = Skill.Type.valueOf(data[0]);
        int level       = Integer.parseInt(data[1]);

        Optional<SkillCategory> category = plugin.getSettingManager().getCategoryByType(type);
        return category.map(skillCategory -> skillCategory.getLevels().get(level - 1).getSkill()).orElse(null);
    }

    public static Skill getSkillFromType(Skill.Type type, int level){
        switch (type.getImplementation()){
            case ATTRIBUTE:{
                return new AttributeSkill(type, level);
            }

            case BEACON:{
                return new BeaconSkill(type, level);
            }

            case STORAGE:{
                return new StorageSkill(type, level);
            }
        }

        return null;
    }

}
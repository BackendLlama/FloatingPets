package gq.zunarmc.spigot.floatingpets.model.config;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigDefinition {

    private final FileConfiguration configuration;
    private final FloatingPets plugin;

    public ConfigDefinition(FloatingPets plugin, FileConfiguration configuration){
        this.plugin        = plugin;
        this.configuration = configuration;
    }

    public boolean isExcludedWorld(String name){
        if(!plugin.isSetting(Setting.WORLD_FILTER))
            return false;

        return configuration.getStringList("settings.world_filter.excluded").contains(name);
    }

}
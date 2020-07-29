package net.llamasoftware.spigot.floatingpets.manager.config;

import net.llamasoftware.spigot.floatingpets.FloatingPets;
import net.llamasoftware.spigot.floatingpets.model.config.YAMLFile;

import java.io.File;

public class YAMLManager {

    private final FloatingPets plugin;

    public YAMLManager(FloatingPets plugin){
        this.plugin = plugin;
    }

    public YAMLFile loadIfNotExists(String name){
        YAMLFile configuration = new YAMLFile(plugin, plugin.getDataFolder() + File.separator + name);

        if(!configuration.exists())
            configuration.create();

        configuration.load();
        return configuration;
    }

}
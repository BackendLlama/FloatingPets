package gq.zunarmc.spigot.floatingpets.helper;

import gq.zunarmc.spigot.floatingpets.FloatingPets;
import org.bukkit.event.Listener;

public class RegistrationHelper {

    private final FloatingPets plugin;
    public RegistrationHelper(FloatingPets plugin){
        this.plugin = plugin;
    }

    public void registerListener(Listener listener){
        plugin.getLogger().info("Registering listener " + listener.getClass().getSimpleName());
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

}
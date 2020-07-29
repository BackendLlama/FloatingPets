package net.llamasoftware.spigot.floatingpets.listener;

import net.llamasoftware.spigot.floatingpets.Constants;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleListener implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event){
        Entity exited = event.getExited();
        if(exited.hasMetadata(Constants.METADATA_NAME_TAG)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event){
        Entity vehicle = event.getVehicle();
        Entity entity  = event.getEntered();
        if(entity instanceof Chicken && vehicle.hasMetadata(Constants.METADATA_PET)){
            event.setCancelled(true);
        }
    }

}
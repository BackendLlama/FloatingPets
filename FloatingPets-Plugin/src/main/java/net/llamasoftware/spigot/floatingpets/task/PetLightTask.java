package net.llamasoftware.spigot.floatingpets.task;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import org.bukkit.Location;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;

public class PetLightTask implements Runnable {

    private final Pet pet;
    private Location previousLocation;

    private static final int LIGHT_LEVEL = 8;
    private static final LightType LIGHT_TYPE = LightType.BLOCK;

    public PetLightTask(Pet pet){
        this.pet = pet;
    }

    @Override
    public void run() {
        if(pet == null || pet.getEntity() == null || pet.getLocation() == null || !pet.isLight()) {
            if(previousLocation != null){
                LightAPI.deleteLight(previousLocation, LIGHT_TYPE, false);
                updateLight(previousLocation);
                previousLocation = null;
            }
            return;
        }

        Location location = pet.getLocation();

        if(previousLocation == null) {
            createLight(location);
            return;
        }

        if(previousLocation.distance(location) >= 5) {
            if (previousLocation != null) {
                LightAPI.deleteLight(previousLocation, LIGHT_TYPE, false);
                updateLight(previousLocation);
            }

            createLight(location);
        }
    }

    private void createLight(Location location){
        LightAPI.createLight(location, LIGHT_TYPE, LIGHT_LEVEL, false);
        updateLight(location);

        previousLocation = location;
    }

    private void updateLight(Location location){
        LightAPI.collectChunks(location, LIGHT_TYPE, LIGHT_LEVEL)
                .forEach(info -> LightAPI.updateChunk(info, LIGHT_TYPE));
    }

}
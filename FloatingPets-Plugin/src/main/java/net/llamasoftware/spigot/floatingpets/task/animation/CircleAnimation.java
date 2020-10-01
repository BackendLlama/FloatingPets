package net.llamasoftware.spigot.floatingpets.task.animation;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CircleAnimation extends PetAnimation {

    private int eX;

    public CircleAnimation(Pet pet, Player player) {
        super(pet, player);
    }

    @Override
    public void animate() {
        Location location = player.getLocation();

        double radians = Math.toRadians(eX);
        double x       = Math.cos(radians);
        double z       = Math.sin(radians);

        location.add(x, 0, z);
        location.setYaw((float) eX);
        pet.getNameTag().teleport(location);
        location.subtract(x, 0, z);

        eX += 2;

        if (eX == 360)
            eX = 0;
    }

}

package net.llamasoftware.spigot.floatingpets.task.animation;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.PetAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FloatAnimation extends PetAnimation {

    private boolean moveDown;

    public FloatAnimation(Pet pet, Player player) {
        super(pet, player);
    }

    @Override
    public void animate() {

        Location location = pet.getNameTag().getLocation();
        pet.getNameTag().teleport(location.add(0, (moveDown ? -1:1) * 0.06, 0));

        if(location.getY() >= (player.getLocation().getY() + player.getHeight()/2)){
            moveDown = true;
        }

        if(location.getY() <= player.getLocation().getY()-0.3){
            moveDown = false;
        }

    }

}

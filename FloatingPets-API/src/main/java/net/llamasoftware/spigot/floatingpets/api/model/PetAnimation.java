package net.llamasoftware.spigot.floatingpets.api.model;

import org.bukkit.entity.Player;

public abstract class PetAnimation {

    public final Pet pet;
    public final Player player;

    public PetAnimation(Pet pet, Player player){
        this.pet    = pet;
        this.player = player;
    }

    public void run() {
        if(!pet.isStill())
            return;

        animate();
    }

    public abstract void animate();

}
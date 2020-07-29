package net.llamasoftware.spigot.floatingpets.task;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class PetHealthRegenerationTask implements Runnable {

    private final Pet pet;

    public PetHealthRegenerationTask(Pet pet){
        this.pet = pet;
    }

    @Override
    public void run() {

        if(pet == null || pet.getEntity() == null)
            return;

        LivingEntity livingEntity = pet.getEntity().getEntity();
        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if(maxHealth == null)
            return;

        if(livingEntity.isDead() || livingEntity.getHealth() <= 0)
            return;

        if(livingEntity.getHealth() == maxHealth.getValue())
            return;

        livingEntity.setHealth(Math.min(livingEntity.getHealth() + 0.5,
                maxHealth.getValue()));

    }

}
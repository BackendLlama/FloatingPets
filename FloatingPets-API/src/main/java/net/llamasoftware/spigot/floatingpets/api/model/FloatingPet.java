package net.llamasoftware.spigot.floatingpets.api.model;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface FloatingPet {

    LivingEntity getEntity();

    void transformEntity();

    void spawnPet(Location location);

    Pet getPet();

    boolean hasTarget();

    boolean hasTarget(Entity entity);

    void removeTarget();

    double getEntityHealth();

    String getSetting(Setting setting);

    boolean isSetting(Setting setting);

    void teleportToOwner();

    void kill();

}
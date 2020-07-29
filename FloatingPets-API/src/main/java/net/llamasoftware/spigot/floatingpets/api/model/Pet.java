package net.llamasoftware.spigot.floatingpets.api.model;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Pet {

    UUID getUniqueId();

    UUID getOwner();

    String getName();

    PetType getType();

    FloatingPet getEntity();

    void setEntity(FloatingPet pet);

    Entity getNameTag();

    void setNameTag(Entity nameTag);

    boolean hasParticle();

    Particle getParticle();

    Player getOnlineOwner();

    Location getLocation();

    List<Skill> getSkills();

    Optional<Skill> getSkillOfType(Skill.Type type);

    void remove();

    void setName(String name);

    void setParticle(Particle particle);

    void attachNameTag();

    boolean isAlive();

    boolean hasPassenger(Entity entity);

    boolean isLight();

    void setLight(boolean light);

    void ride(Entity entity);

    void stopRiding(Entity entity);

    boolean isRiding(Entity entity);

}
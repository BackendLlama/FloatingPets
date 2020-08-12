package net.llamasoftware.spigot.floatingpets.model.pet;

import net.llamasoftware.spigot.floatingpets.api.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@Builder
public class IPet implements Pet {

    @Getter
    private final UUID uniqueId;
    @Getter
    private String name;
    @Getter
    private final UUID owner;
    @Getter
    private final PetType type;
    @Getter
    private final List<Skill> skills;
    @Getter @Setter
    private FloatingPet entity;
    @Getter @Setter
    private Entity nameTag;
    @Getter
    private Particle particle;
    @Getter @Setter
    private boolean light;
    private Map<String, Object> extra;

    @Override
    public void remove() {
        if(entity != null)
            entity.getEntity().remove();

        if(nameTag != null)
            nameTag.remove();

        entity          = null;
        nameTag         = null;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setParticle(Particle particle) {
        if(getParticle() != null)
            getParticle().stop();

        this.particle = particle;
        if(this.particle != null)
            this.particle.start();
    }

    @Override
    public void attachNameTag() {
        // nameTagExtender.addPassenger(nameTag);
        // entity.getEntity().addPassenger(nameTagExtender);
    }

    @Override
    public boolean isAlive() {
        return entity != null && entity.getEntity() != null &&
                nameTag != null && !entity.getEntity().isDead() && !nameTag.isDead();
    }

    @Override
    public boolean hasPassenger(Entity entity) {
        return getEntity().getEntity().getPassengers().contains(entity);
    }

    @Override
    public void ride(Entity entity) {
        getOnlineOwner().addPassenger(nameTag);
        nameTag.addPassenger(getEntity().getEntity());
    }

    @Override
    public void stopRiding(Entity entity) {
        getOnlineOwner().removePassenger(nameTag);
        nameTag.removePassenger(getEntity().getEntity());
    }

    @Override
    public boolean isRiding(Entity entity) {
        return entity.getPassengers().stream()
                .anyMatch(ent -> ent.equals(this.getNameTag()));
    }

    @Override
    public Map<String, Object> getExtra() {
        return extra;
    }

    @Override
    public void setExtra(String key, Object object) {
        if(extra == null)
            extra = new HashMap<>();

        extra.put(key, object);
    }

    @Override
    public Object getExtra(String key) {
        return extra.get(key);
    }

    @Override
    public boolean hasParticle() {
        return getParticle() != null;
    }

    @Override
    public Player getOnlineOwner() {
        return Bukkit.getPlayer(owner);
    }

    @Override
    public Location getLocation() {
        return getEntity().getEntity().getLocation();
    }

    @Override
    public Optional<Skill> getSkillOfType(Skill.Type type) {
        if(skills == null)
            return Optional.empty();

        return skills.stream()
                .filter(Objects::nonNull)
                .filter(skill -> skill.getType() == type)
                .findAny();
    }

}
package gq.zunarmc.spigot.floatingpets.model.pet;

import gq.zunarmc.spigot.floatingpets.api.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        return skills.stream()
                .filter(skill -> skill.getType() == type)
                .findAny();
    }

}
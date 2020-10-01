package net.llamasoftware.spigot.floatingpets.nms.v1_15_R1.pathfinder;

import net.llamasoftware.spigot.floatingpets.api.model.FloatingPet;
import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.llamasoftware.spigot.floatingpets.api.model.Setting;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.PathEntity;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    private final FloatingPet pet;
    private final EntityInsentient entity;
    private final Player owner;
    private final double speed;

    public PathfinderGoalFollowOwner(FloatingPet pet, Player owner, double speed){
        this.pet    = pet;
        this.entity = (EntityInsentient) pet;
        this.owner  = owner;
        this.speed  = speed;
    }

    @Override
    public boolean a() {
        if(this.entity.getGoalTarget() != null)
            return false;

        if(pet.isSetting(Setting.PET_TELEPORTATION_DISTANCE)
                && inSameWorld()
                && (this.owner.getLocation().distance(this.entity.getBukkitEntity().getLocation())
                >= Double.parseDouble(pet.getSetting(Setting.PET_TELEPORTATION_DISTANCE_DISTANCE)))) {

            this.entity.setLocation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                    owner.getLocation().getYaw(), owner.getLocation().getPitch());
        }

        c();
        return true;
    }

    @Override
    public void c() {

        Entity bukkitEntity = entity.getBukkitEntity();
        Pet pet = this.pet.getPet();

        if (!bukkitEntity.getWorld().getName().equals(owner.getWorld().getName())){
            bukkitEntity.teleport(owner.getLocation());
        }

        if(((LivingEntity) bukkitEntity).isLeashed())
            return;

        if(pet.getNameTag().getPassengers().stream()
                .anyMatch(passenger -> passenger instanceof Player))
            return;

        double distance = this.entity.getBukkitEntity().getLocation().distance(owner.getLocation());

        if(inSameWorld() && distance > 2.5) {
            PathEntity path = entity.getNavigation().a(owner.getLocation().getX() + 1, owner.getLocation().getY(),
                    owner.getLocation().getZ() - 1,1);

            entity.getNavigation().a(path, speed);
            // TODO move code like this to plugin level
            pet.setLastMove(System.currentTimeMillis());

            if(pet.isStill())
                pet.setStill(false);

        } else if (distance <= 2){
            if(!pet.isStill() && (System.currentTimeMillis() - pet.getLastMove()) > 1500)
                pet.setStill(true);
        }

    }

    private boolean inSameWorld(){
        World ownerWorld = owner.getLocation().getWorld();
        if(ownerWorld == null)
            return true;

        World petWorld = entity.getBukkitEntity().getLocation().getWorld();

        return ownerWorld.equals(petWorld);
    }

}
package gq.zunarmc.spigot.floatingpets.nms.v1_16_R1.pathfinder;

import gq.zunarmc.spigot.floatingpets.api.model.FloatingPet;
import gq.zunarmc.spigot.floatingpets.api.model.Setting;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.PathEntity;
import net.minecraft.server.v1_16_R1.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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

        if (!bukkitEntity.getWorld().getName().equals(owner.getWorld().getName())){
            bukkitEntity.teleport(owner.getLocation());
        }

        if(((LivingEntity) bukkitEntity).isLeashed())
            return;

        if(bukkitEntity.getPassengers().stream()
                .anyMatch(passenger -> passenger instanceof Player))
            return;

        if(inSameWorld() && this.entity.getBukkitEntity().getLocation().distance(owner.getLocation()) >= 1.5) {
            PathEntity path = entity.getNavigation().a(owner.getLocation().getX() + 1, owner.getLocation().getY(),
                    owner.getLocation().getZ() - 1,1);

            entity.getNavigation().a(path, speed);

        } else {
            if(ownerIsLooking()) {
                entity.getControllerLook().a(owner.getLocation().getX(),
                        owner.getLocation().getY() + ((CraftPlayer) owner).getHandle().getHeadHeight(), owner.getLocation().getZ());
            }
        }

    }

    private boolean ownerIsLooking(){

        Location eye = owner.getEyeLocation();
        Vector toEntity = ((LivingEntity) entity.getBukkitEntity()).getEyeLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > 0.99D;

    }

    private boolean inSameWorld(){
        World ownerWorld = owner.getLocation().getWorld();
        if(ownerWorld == null)
            return true;

        World petWorld = entity.getBukkitEntity().getLocation().getWorld();

        return ownerWorld.equals(petWorld);
    }

}
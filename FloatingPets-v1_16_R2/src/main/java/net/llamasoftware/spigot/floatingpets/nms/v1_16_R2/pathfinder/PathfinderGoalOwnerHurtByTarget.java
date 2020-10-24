package net.llamasoftware.spigot.floatingpets.nms.v1_16_R2.pathfinder;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int c;

    private final Pet pet;

    public PathfinderGoalOwnerHurtByTarget(EntityCreature zombie, Pet pet) {
        super(zombie, false);
        this.owner = ((CraftPlayer) pet.getOnlineOwner()).getHandle();
        this.pet   = pet;
        this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
    }

    @Override
    public boolean a() {
        if(owner == null)
            return false;

        this.target = owner.getLastDamager();
        int i = owner.cZ();

        return i != this.c && this.a(this.target, PathfinderTargetCondition.a);
    }

    @Override
    public void c() {
        if(target == null)
            return;

        pet.setStill(false);
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        EntityLiving entityliving = owner;

        if (entityliving != null) {
            this.c = entityliving.cZ();
        }

        super.c();
    }

}
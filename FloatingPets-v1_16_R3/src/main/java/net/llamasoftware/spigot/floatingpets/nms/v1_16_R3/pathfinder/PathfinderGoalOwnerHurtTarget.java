package net.llamasoftware.spigot.floatingpets.nms.v1_16_R3.pathfinder;

import net.llamasoftware.spigot.floatingpets.api.model.Pet;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

    private EntityLiving target;
    private int c;

    private final EntityLiving owner;
    private final Pet pet;

    public PathfinderGoalOwnerHurtTarget(EntityCreature zombie, Pet pet) {
        super(zombie, false);
        this.pet   = pet;
        this.owner = ((CraftPlayer) pet.getOnlineOwner()).getHandle();
        this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
    }

    @Override
    public boolean a() {

        EntityLiving entityliving = this.owner;

        if (entityliving == null) {
            return false;
        } else {
            this.target = entityliving.db();
            int i = entityliving.dc();

            return i != this.c && this.a(this.target, PathfinderTargetCondition.a);
        }

    }

    @Override
    public void c() {
        if(this.target == null || this.e == null) {
            return;
        }

        pet.setStill(false);
        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        EntityLiving entityliving = owner;

        if (entityliving != null) {
            this.c = entityliving.dc();
        }

        super.c();
    }

}

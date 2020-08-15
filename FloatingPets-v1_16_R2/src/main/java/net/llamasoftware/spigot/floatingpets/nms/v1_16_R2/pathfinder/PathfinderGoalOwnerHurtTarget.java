package net.llamasoftware.spigot.floatingpets.nms.v1_16_R2.pathfinder;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtTarget extends PathfinderGoalTarget {

    private EntityLiving target;
    private int c;

    private final EntityLiving owner;

    public PathfinderGoalOwnerHurtTarget(EntityCreature zombie, EntityLiving owner) {
        super(zombie, false);
        this.owner = owner;
        this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
    }

    @Override
    public boolean a() {

        EntityLiving entityliving = this.owner;

        if (entityliving == null) {
            return false;
        } else {
            this.target = entityliving.da();
            int i = entityliving.db();

            return i != this.c && this.a(this.target, PathfinderTargetCondition.a);
        }

    }

    @Override
    public void c() {
        if(this.target == null || this.e == null) {
            return;
        }

        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        EntityLiving entityliving = owner;

        if (entityliving != null) {
            this.c = entityliving.db();
        }

        super.c();
    }

}
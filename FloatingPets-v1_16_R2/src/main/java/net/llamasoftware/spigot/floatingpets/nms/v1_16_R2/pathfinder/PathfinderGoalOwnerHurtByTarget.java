package net.llamasoftware.spigot.floatingpets.nms.v1_16_R2.pathfinder;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class PathfinderGoalOwnerHurtByTarget extends PathfinderGoalTarget {

    private final EntityLiving owner;
    private EntityLiving target;
    private int c;

    public PathfinderGoalOwnerHurtByTarget(EntityCreature zombie, EntityLiving owner) {
        super(zombie, false);
        this.owner = owner;
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

        this.e.setGoalTarget(this.target, EntityTargetEvent.TargetReason.CUSTOM, false);
        EntityLiving entityliving = owner;

        if (entityliving != null) {
            this.c = entityliving.cZ();
        }

        super.c();
    }

}
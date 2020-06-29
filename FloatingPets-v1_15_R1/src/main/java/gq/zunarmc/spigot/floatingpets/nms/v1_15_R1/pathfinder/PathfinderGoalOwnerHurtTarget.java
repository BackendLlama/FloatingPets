package gq.zunarmc.spigot.floatingpets.nms.v1_15_R1.pathfinder;

import net.minecraft.server.v1_15_R1.*;
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
            this.target = entityliving.cJ();
            int i = entityliving.cK();

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
            this.c = entityliving.cK();
        }

        super.c();
    }

}

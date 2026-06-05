package com.jackson.tricksandtrials.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BossBowAttackGoal extends Goal {

    private final WarriorBossEntity boss;
    private int attackCooldown = 0;
    private static final int SHOOT_COOLDOWN = 8;   // ticks between shots
    private static final double BOW_RANGE   = 20.0; // max shooting distance
    private static final double RETREAT_DIST = 12.0; // back off if closer than this

    public BossBowAttackGoal(WarriorBossEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return boss.getTarget() != null && boss.getCombatMode() == 1;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = boss.getTarget();
        if (target == null) return;

        double distSq = boss.distanceToSqr(target);

        // No chasing. Hold ground; only retreat if the player closes in.
        if (distSq < RETREAT_DIST * RETREAT_DIST) {
            double dx = boss.getX() - target.getX();
            double dz = boss.getZ() - target.getZ();
            boss.getNavigation().moveTo(boss.getX() + dx, boss.getY(), boss.getZ() + dz, 1.0);
        } else {
            boss.getNavigation().stop();
        }

        boss.getLookControl().setLookAt(target, 30f, 30f);

        attackCooldown--;
        if (attackCooldown <= 0
                && distSq <= BOW_RANGE * BOW_RANGE
                && boss.getSensing().hasLineOfSight(target)) {
            attackCooldown = SHOOT_COOLDOWN;
            boss.performRangedAttack(target, 0.8f);
        }
    }

    @Override
    public void stop() {
        attackCooldown = 0;
        boss.getNavigation().stop();
    }
}
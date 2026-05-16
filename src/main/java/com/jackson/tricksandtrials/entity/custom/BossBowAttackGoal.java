package com.jackson.tricksandtrials.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BossBowAttackGoal extends Goal {

    private final WarriorBossEntity boss;
    private int attackCooldown = 0;
    private static final int SHOOT_COOLDOWN = 40; // ticks between shots
    private static final double BOW_RANGE = 20.0;

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

        // Stay at range — move closer if too far, back off if too close
        if (distSq > BOW_RANGE * BOW_RANGE) {
            boss.getNavigation().moveTo(target, 1.1);
        } else if (distSq < 6 * 6) {
            boss.getNavigation().moveTo(target, 1.1); // still advance slowly
        } else {
            boss.getNavigation().stop();
        }

        boss.getLookControl().setLookAt(target, 30f, 30f);

        attackCooldown--;
        if (attackCooldown <= 0) {
            attackCooldown = SHOOT_COOLDOWN;
            boss.performRangedAttack(target, 3);
        }
    }

    @Override
    public void stop() {
        attackCooldown = 0;
    }
}

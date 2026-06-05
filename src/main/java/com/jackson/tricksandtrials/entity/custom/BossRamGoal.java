package com.jackson.tricksandtrials.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class BossRamGoal extends Goal {


    private enum Phase { PREPARE, CHARGE }

    private final WarriorBossEntity boss;

    private static final double MIN_RAM_DISTANCE = 4.0;
    private static final int    PREPARE_TICKS    = 22;
    private static final int    MIN_CHARGE_TICKS = 5;   // ignore collisions before this
    private static final int    MAX_CHARGE_TICKS = 50;
    private static final double CHARGE_SPEED     = 3;
    private static final double KNOCKBACK        = 2.5;

    private Phase phase;
    private int phaseTimer;
    private Vec3 chargeDir = Vec3.ZERO;

    public BossRamGoal(WarriorBossEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }




    @Override
    public boolean canUse() {
        LivingEntity target = boss.getTarget();
        if (target == null) return false;
        // far enough away (this also implies bow mode given the distance-based switch)
        if (boss.distanceToSqr(target) < MIN_RAM_DISTANCE * MIN_RAM_DISTANCE) return false;
        return boss.isRamReady();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = boss.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (phase == Phase.CHARGE) {
            if (phaseTimer >= MAX_CHARGE_TICKS) return false;
            if (phaseTimer > MIN_CHARGE_TICKS && boss.horizontalCollision) return false;
        }
        return true;
    }

    @Override
    public void start() {
        phase = Phase.PREPARE;
        phaseTimer = 0;
        boss.getNavigation().stop();
        LivingEntity target = boss.getTarget();
        if (target != null) boss.getLookControl().setLookAt(target, 30f, 30f);
    }

    @Override
    public void tick() {
        LivingEntity target = boss.getTarget();
        if (target == null) return;
        phaseTimer++;

        if (phase == Phase.PREPARE) {
            boss.getNavigation().stop();
            boss.setDeltaMovement(boss.getDeltaMovement().multiply(0.2, 1.0, 0.2)); // stand still
            boss.getLookControl().setLookAt(target, 30f, 30f);

            if (phaseTimer == 1) {
                boss.level().playSound(null, boss, SoundEvents.GOAT_PREPARE_RAM,
                        SoundSource.HOSTILE, 1.5f, 1.0f);
            }
            if (phaseTimer >= PREPARE_TICKS) {
                Vec3 dir = new Vec3(target.getX() - boss.getX(), 0.0, target.getZ() - boss.getZ());
                chargeDir = dir.lengthSqr() < 1.0e-4 ? boss.getForward() : dir.normalize();
                phase = Phase.CHARGE;
                phaseTimer = 0;
                boss.level().playSound(null, boss, SoundEvents.RAVAGER_ROAR,
                        SoundSource.HOSTILE, 1.0f, 1.2f);
            }
            return;
        }

        // CHARGE — straight-line lunge
        Vec3 m = boss.getDeltaMovement();
        boss.setDeltaMovement(chargeDir.x * CHARGE_SPEED, m.y, chargeDir.z * CHARGE_SPEED);
        boss.hasImpulse = true;


        float yaw = (float) (Math.atan2(chargeDir.z, chargeDir.x) * (180.0 / Math.PI)) - 90.0f;
        boss.setYRot(yaw);
        boss.yBodyRot = yaw;

        List<LivingEntity> hits = boss.level().getEntitiesOfClass(
                LivingEntity.class, boss.getBoundingBox().inflate(0.5),
                e -> e != boss && !(e instanceof WarriorBossEntity) && e.isAlive());

        if (!hits.isEmpty()) {
            LivingEntity hit = hits.get(0);
            DamageSource src = boss.damageSources().mobAttack(boss);
            hit.hurt(src, (float) boss.getAttributeValue(Attributes.ATTACK_DAMAGE));
            hit.knockback(KNOCKBACK, boss.getX() - hit.getX(), boss.getZ() - hit.getZ());
            boss.level().playSound(null, boss, SoundEvents.GOAT_RAM_IMPACT,
                    SoundSource.HOSTILE, 1.0f, 1.0f);
            phaseTimer = MAX_CHARGE_TICKS; // end charge
        }
    }
    @Override
    public void stop() {
        boss.onRamFinished();   // was: lastRamGameTime = boss.level().getGameTime();
        boss.setDeltaMovement(boss.getDeltaMovement().multiply(0.2, 1.0, 0.2));
        boss.getNavigation().stop();
        chargeDir = Vec3.ZERO;
    }
}
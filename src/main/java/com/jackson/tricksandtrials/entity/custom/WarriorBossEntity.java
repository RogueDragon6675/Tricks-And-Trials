package com.jackson.tricksandtrials.entity.custom;

import com.jackson.tricksandtrials.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class WarriorBossEntity extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Integer> COMBAT_MODE =
            SynchedEntityData.defineId(WarriorBossEntity.class, EntityDataSerializers.INT);

    private static final int MODE_SWITCH_MIN = 60;
    private static final int MODE_SWITCH_MAX = 140;

    private int modeSwitchTimer = 0;
    private int nextModeSwitch  = 100;

    public WarriorBossEntity(EntityType<? extends WarriorBossEntity> type, Level level) {
        super(type, level);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WARRIORS_BOW.get()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH,           300.0)
                .add(Attributes.MOVEMENT_SPEED,        0.32)
                .add(Attributes.ATTACK_DAMAGE,         10.0)
                .add(Attributes.FOLLOW_RANGE,          48.0)
                .add(Attributes.ARMOR,                  8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE,  0.75);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COMBAT_MODE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BossBowAttackGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Raider.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            updateCombatMode();
        }
    }

    private void updateCombatMode() {
        LivingEntity target = this.getTarget();

        if (target == null) {
            setCombatMode(0);
            modeSwitchTimer = 0;
            return;
        }

        modeSwitchTimer++;
        if (modeSwitchTimer >= nextModeSwitch) {
            modeSwitchTimer = 0;
            nextModeSwitch  = MODE_SWITCH_MIN + random.nextInt(MODE_SWITCH_MAX - MODE_SWITCH_MIN);

            int newMode = random.nextBoolean() ? 1 : 2;
            setCombatMode(newMode);

            if (newMode == 1) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WARRIORS_BOW.get()));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WARRIORS_AXE.get()));
            }
        }
    }

    // ── Ranged attack (bow mode) ───────────────────────────────────────────────
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ItemStack bowStack = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, bowStack, power, this.getMainHandItem());
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333) - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        arrow.shoot(dx, dy + horizDist * 0.2, dz, 1.6f, 4.0f);
        this.level().addFreshEntity(arrow);
    }

    public int getCombatMode() {
        return entityData.get(COMBAT_MODE);
    }

    private void setCombatMode(int mode) {
        entityData.set(COMBAT_MODE, mode);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        // 1.5x pillager (0.6w x 1.95h) → 0.9w x 2.925h
        return EntityDimensions.scalable(0.9f, 2.925f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CombatMode", getCombatMode());
        tag.putInt("ModeSwitchTimer", modeSwitchTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setCombatMode(tag.getInt("CombatMode"));
        modeSwitchTimer = tag.getInt("ModeSwitchTimer");
    }

}

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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;

public class WarriorBossEntity extends Monster implements RangedAttackMob {

    private final ServerBossEvent bossEvent =
            new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    private static final EntityDataAccessor<Integer> COMBAT_MODE =
            SynchedEntityData.defineId(WarriorBossEntity.class, EntityDataSerializers.INT);
    private int arrowsSinceRam = 0;
    private int arrowsUntilRam = 0; // rolled lazily: 3, 4, or 5

    private static final int MIN_ARROWS_BEFORE_RAM = 3;
    private static final int MAX_ARROWS_BEFORE_RAM = 8;

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
                .add(Attributes.STEP_HEIGHT, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE,  0.75);
    }

    private void rollArrowsUntilRam() {
        arrowsUntilRam = MIN_ARROWS_BEFORE_RAM + random.nextInt(MAX_ARROWS_BEFORE_RAM+1-MIN_ARROWS_BEFORE_RAM); // 3–5
    }

    public boolean isRamReady() {
        if (arrowsUntilRam == 0) rollArrowsUntilRam();
        return arrowsSinceRam >= arrowsUntilRam;
    }

    public void onRamFinished() {
        arrowsSinceRam = 0;
        rollArrowsUntilRam();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COMBAT_MODE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BossRamGoal(this));
        this.goalSelector.addGoal(2, new BossBowAttackGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

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
            bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }
    private void updateCombatMode() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            if (getCombatMode() != 0) setCombatMode(0);
            return;
        }

        double dist = this.distanceTo(target);
        int current = getCombatMode();
        int mode = current;

        if (current == 0) {
            mode = 1;              // default to bow the instant we acquire a target
        } else if (dist > 7.0) {
            mode = 1;              // far  → bow
        } else if (dist < 5.0) {
            mode = 2;              // close → axe
        }
        // 5–7 blocks: keep current mode (hysteresis, stops rapid flip-flopping)

        if (mode != current) {
            setCombatMode(mode);
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(
                    mode == 2 ? ModItems.WARRIORS_AXE.get() : ModItems.WARRIORS_BOW.get()));
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

        arrow.shoot(dx, dy + horizDist * 0.2, dz, 1.6f, 4.0f);
        this.level().addFreshEntity(arrow);
        arrowsSinceRam++;   // <-- add this
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setCombatMode(tag.getInt("CombatMode"));
    }
}
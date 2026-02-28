package com.jackson.tricksandtrials.event;

import com.jackson.tricksandtrials.TricksandTrials;

import com.jackson.tricksandtrials.enchantment.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = TricksandTrials.MODID)
public class ModEvents {
    private static final Map<UUID, Integer> autoFireCooldowns = new HashMap<>();
    private static final Map<UUID, TargetData> homingTargets = new HashMap<>();

    // Store target info for each arrow
    private static class TargetData {
        UUID targetUUID;
        int homingLevel;

        TargetData(UUID targetUUID, int homingLevel) {
            this.targetUUID = targetUUID;
            this.homingLevel = homingLevel;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        UUID playerId = player.getUUID();

        if ((mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem)
                && player.isUsingItem()) {
            int rapidFireLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    player.level().holderOrThrow(ModEnchantments.QUICK_SHOT), mainHand);

            if (rapidFireLevel > 0) {
                int cooldown = autoFireCooldowns.getOrDefault(playerId, 0);

                if (cooldown <= 0) {
                    fireArrow(player, mainHand);

                    int fireCooldown = Math.max(1, 6 - (2 * rapidFireLevel));
                    autoFireCooldowns.put(playerId, fireCooldown);
                } else {
                    autoFireCooldowns.put(playerId, cooldown - 1);
                }
            }
        } else {
            autoFireCooldowns.put(playerId, 0);
        }
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof Player player) {
            if (!event.getLevel().isClientSide()) {
                ItemStack bow = player.getMainHandItem();

                int homingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                        player.level().holderOrThrow(ModEnchantments.HOMING), bow);

                if (homingLevel > 0) {
                    // Find what the player is looking at (fixed range)
                    LivingEntity target = getTargetEntity(player);

                    if (target != null) {
                        homingTargets.put(arrow.getUUID(), new TargetData(target.getUUID(), homingLevel));

                        // Visual/audio feedback that target was locked
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                                0.3F, 2.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onArrowTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (event.getEntity() instanceof AbstractArrow arrow && !arrow.level().isClientSide()) {
            TargetData targetData = homingTargets.get(arrow.getUUID());

            if (targetData != null && !arrow.onGround() && arrow.tickCount > 1) {
                // Find the target entity by UUID
                Entity targetEntity = ((ServerLevel) arrow.level()).getEntity(targetData.targetUUID);

                if (targetEntity instanceof LivingEntity target && target.isAlive()) {
                    // Calculate direction to target (aim for center mass)
                    Vec3 arrowPos = arrow.position();
                    Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);

                    // Predict target position based on their velocity
                    Vec3 targetVelocity = target.getDeltaMovement();
                    double predictionTicks = arrowPos.distanceTo(targetPos) / arrow.getDeltaMovement().length();
                    Vec3 predictedPos = targetPos.add(targetVelocity.scale(Math.min(predictionTicks, 10)));

                    Vec3 directionToTarget = predictedPos.subtract(arrowPos).normalize();

                    // Get current velocity
                    Vec3 currentVelocity = arrow.getDeltaMovement();
                    double speed = currentVelocity.length();

                    // Rotation speed based on level (how fast it turns toward target)
                    // Much stronger values for visible homing
                    double rotationSpeed = 0.15 + (targetData.homingLevel * 0.15);
                    // Level 1: 0.30, Level 2: 0.45, Level 3: 0.60

                    // Blend current direction with target direction
                    Vec3 newVelocity = currentVelocity.normalize()
                            .add(directionToTarget.scale(rotationSpeed))
                            .normalize()
                            .scale(speed);

                    arrow.setDeltaMovement(newVelocity);

                    // Update arrow rotation to match new direction
                    double horizontalDistance = Math.sqrt(newVelocity.x * newVelocity.x + newVelocity.z * newVelocity.z);
                    arrow.setYRot((float)(Math.atan2(newVelocity.x, newVelocity.z) * 180.0 / Math.PI));
                    arrow.setXRot((float)(Math.atan2(newVelocity.y, horizontalDistance) * 180.0 / Math.PI));

                    // Set previous rotation for smooth interpolation
                    arrow.yRotO = arrow.getYRot();
                    arrow.xRotO = arrow.getXRot();

                    // Add particle trail for visual feedback (optional)
                    if (arrow.level().random.nextFloat() < 0.3f) {
                        ((ServerLevel) arrow.level()).sendParticles(
                                net.minecraft.core.particles.ParticleTypes.CRIT,
                                arrow.getX(), arrow.getY(), arrow.getZ(),
                                1, 0, 0, 0, 0
                        );
                    }
                } else {
                    // Target is dead or gone, stop tracking
                    homingTargets.remove(arrow.getUUID());
                }
            }

            // Clean up if arrow is on ground or removed
            if (arrow.onGround() || arrow.isRemoved()) {
                homingTargets.remove(arrow.getUUID());
            }
        }
    }

    // Raycast to find what entity the player is looking at
    private static LivingEntity getTargetEntity(Player player) {
        double maxDistance = 50.0; // Fixed 50 block range for lock-on

        Vec3 start = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 end = start.add(lookVec.scale(maxDistance));

        // Check for entity intersection
        EntityHitResult entityHit = getEntityHit(player, start, end);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            return target;
        }

        return null;
    }

    // Helper method to raycast for entities
    private static EntityHitResult getEntityHit(Player player, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();

        AABB searchBox = player.getBoundingBox().expandTowards(direction).inflate(1.0);

        Entity closestEntity = null;
        double closestDistance = distance;

        for (Entity entity : player.level().getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive() && e.isPickable())) {

            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            var hit = entityBox.clip(start, end);

            if (hit.isPresent()) {
                double dist = start.distanceTo(hit.get());
                if (dist < closestDistance) {
                    closestEntity = entity;
                    closestDistance = dist;
                }
            }
        }

        if (closestEntity != null) {
            return new EntityHitResult(closestEntity);
        }

        return null;
    }

    private static void fireArrow(Player player, ItemStack bow) {
        ServerLevel level = (ServerLevel) player.level();

        ItemStack arrowStack = player.getProjectile(bow);

        if (arrowStack.isEmpty() && player.getAbilities().instabuild) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        if (!arrowStack.isEmpty()) {
            ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ?
                    arrowStack.getItem() : Items.ARROW);

            AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, bow);

            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(),
                    0.0F, 3.0F, 3.0F);

            arrow.setCritArrow(false);

            if (!player.getAbilities().instabuild) {
                arrowStack.shrink(1);
            }

            bow.hurtAndBreak(1, player, player.getEquipmentSlotForItem(bow));

            level.addFreshEntity(arrow);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                    0.5F, 1.5F);
        }
    }
}
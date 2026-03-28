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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = TricksandTrials.MODID)
public class ModEvents {
    private static final Map<UUID, Integer> autoFireCooldowns = new HashMap<>();

    // Maps arrow UUID -> the locked-on target entity UUID + homing level
    private static final Map<UUID, TargetData> homingTargets = new HashMap<>();

    private static class TargetData {
        final UUID targetEntityId; // Locked-on entity (null = home toward block/look)
        final int homingLevel;

        TargetData(UUID targetEntityId, int homingLevel) {
            this.targetEntityId = targetEntityId;
            this.homingLevel = homingLevel;
        }
    }

    // -------------------------------------------------------------------------
    // Quick Shot (auto-fire) logic — unchanged from original
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

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
                    int fireCooldown = Math.max(1, 10 - (2*rapidFireLevel));
                    autoFireCooldowns.put(playerId, fireCooldown);
                } else {
                    autoFireCooldowns.put(playerId, cooldown - 1);
                }
            }
        } else {
            autoFireCooldowns.put(playerId, 0);
        }
    }

    // -------------------------------------------------------------------------
    // Homing: lock on when the arrow spawns
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof Player player)) return;
        if (event.getLevel().isClientSide()) return;

        ItemStack bow = player.getMainHandItem();
        int homingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                player.level().holderOrThrow(ModEnchantments.HOMING), bow);

        if (homingLevel <= 0) return;

        // Try to find an entity the player is looking toward within the search cone
        Optional<LivingEntity> lockedTarget = findLookTarget(player, 60.0);

        UUID targetId = lockedTarget.map(Entity::getUUID).orElse(null);
        homingTargets.put(arrow.getUUID(), new TargetData(targetId, homingLevel));

        // Lock-on sound: pitch varies by whether we actually locked an entity
        float pitch = targetId != null ? 2.0F : 1.5F;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, pitch);
    }

    // -------------------------------------------------------------------------
    // Homing: steer the arrow every tick toward the locked target
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onArrowTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (arrow.level().isClientSide()) return;

        TargetData data = homingTargets.get(arrow.getUUID());
        if (data == null) return;

        // Clean up if the arrow has hit the ground or been removed
        if (arrow.onGround() || arrow.isRemoved()) {
            homingTargets.remove(arrow.getUUID());
            return;
        }

        // Stop homing once the arrow exceeds level * 35 blocks from its owner
        double maxHomingDistance = data.homingLevel * 50.0;
        if (arrow.getOwner() != null && arrow.distanceTo(arrow.getOwner()) > maxHomingDistance) {
            homingTargets.remove(arrow.getUUID());
            return;
        }

        // Wait a brief moment before homing kicks in so the arrow travels naturally first
        if (arrow.tickCount <= 3) return;

        Vec3 targetPos = resolveTargetPos(arrow, data);
        if (targetPos == null) return;

        Vec3 currentVelocity = arrow.getDeltaMovement();
        double speed = currentVelocity.length();
        if (speed < 1e-6) return; // Avoid divide-by-zero on a stationary arrow

        Vec3 desiredDirection = targetPos.subtract(arrow.position()).normalize();

        // Level 5 gets a significantly stronger turn to make it feel near-perfect
        // Other levels scale linearly, capped at 0.20 to stay believable
        double turnStrength = (data.homingLevel == 5)
                ? 0.40
                : Math.min(0.05 * data.homingLevel, 0.20);

        Vec3 newVelocity = currentVelocity.lerp(desiredDirection.scale(speed), turnStrength);

        arrow.setDeltaMovement(newVelocity);
        arrow.hasImpulse = true; // Forces a movement packet to clients

        // Keep rotation in sync with velocity so the arrow visually points correctly
        double horizDist = newVelocity.horizontalDistance();
        float yaw  = (float)(Math.atan2(newVelocity.x, newVelocity.z) * (180.0 / Math.PI));
        float pitch = (float)(Math.atan2(newVelocity.y, horizDist)    * (180.0 / Math.PI));

        arrow.setYRot(yaw);
        arrow.setXRot(pitch);
        arrow.yRotO = yaw;
        arrow.xRotO = pitch;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Finds the closest living entity within {@code maxDistance} blocks that
     * falls inside a cone defined by the player's look direction.
     * Ignores the player themselves and their own mount/passengers.
     */
    private static Optional<LivingEntity> findLookTarget(Player player, double maxDistance) {
        Vec3 eyePos  = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // Broad-phase: grab all living entities in a bounding box around the look ray
        AABB searchBox = new AABB(eyePos, eyePos.add(lookVec.scale(maxDistance)))
                .inflate(4.0); // inflate so wide targets aren't missed

        List<LivingEntity> candidates = player.level()
                .getEntitiesOfClass(LivingEntity.class, searchBox,
                        e -> e != player && e.isAlive() && !e.isAlliedTo(player));

        // Score by how close they are to the centre of the look ray
        return candidates.stream()
                .filter(e -> {
                    Vec3 toEntity = e.position().subtract(eyePos).normalize();
                    double dot = toEntity.dot(lookVec); // 1 = dead ahead, 0 = 90°
                    return dot > 0.8; // ~37° half-angle cone — tweak as needed
                })
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(
                        eyePos.add(lookVec.scale(e.position().subtract(eyePos).dot(lookVec))))));
    }

    /**
     * Returns the world position the arrow should steer toward.
     * If we have a locked entity, we track its current position.
     * Otherwise we fall back to the arrow owner's look direction.
     */
    private static Vec3 resolveTargetPos(AbstractArrow arrow, TargetData data) {
        if (data.targetEntityId != null && arrow.level() instanceof ServerLevel serverLevel) {
            Entity target = serverLevel.getEntity(data.targetEntityId);
            if (target instanceof LivingEntity living && living.isAlive()) {
                // Aim for the chest, not the feet
                return living.position().add(0, living.getBbHeight() * 0.5, 0);
            }
            // Target died — clear the lock so homing stops
            homingTargets.remove(arrow.getUUID());
            return null;
        }

        // No entity locked: home toward wherever the owner is currently looking
        if (arrow.getOwner() instanceof Player player) {
            Vec3 eyePos = player.getEyePosition();
            return eyePos.add(player.getLookAngle().scale(500.0));
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Quick Shot arrow firing
    // -------------------------------------------------------------------------

    private static void fireArrow(Player player, ItemStack bow) {
        ServerLevel level = (ServerLevel) player.level();

        ItemStack arrowStack = player.getProjectile(bow);
        if (arrowStack.isEmpty() && player.getAbilities().instabuild) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        if (arrowStack.isEmpty()) return;

        ArrowItem arrowItem = arrowStack.getItem() instanceof ArrowItem ai ? ai
                : (ArrowItem) Items.ARROW;

        AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, bow);
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 3.0F);
        arrow.setCritArrow(false);

        if (!player.getAbilities().instabuild) {
            arrowStack.shrink(1);
        }

        bow.hurtAndBreak(1, player, player.getEquipmentSlotForItem(bow));
        level.addFreshEntity(arrow);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.5F, 1.5F);
    }
}
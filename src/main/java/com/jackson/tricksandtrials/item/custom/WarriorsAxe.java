package com.jackson.tricksandtrials.item.custom;

import com.jackson.tricksandtrials.entity.custom.ShockwaveEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WarriorsAxe extends AxeItem {

    // ── Line shockwave (right-click) ──────────────────────────────────────────
    // To tune: increase LINE_SPEED to travel faster, LINE_GROWTH_RATE for a wider wave
    private static final float LINE_DAMAGE        = 12f;
    private static final float LINE_SPEED         = 1.5f;   // blocks/tick forward travel  ← SPEED
    private static final float LINE_GROWTH_RATE   = 0.75f;  // radius added per tick       ← WIDTH
    private static final int   LINE_LIFETIME      = 10;     // ticks alive                 ← RANGE
    private static final int   LINE_COOLDOWN      = 25;     // ticks before next use
    private static final float LINE_FORCE_BACK = 6;
    private static final float LINE_FORCE_UP = 2;

    // ── Circle burst (shift right-click) ──────────────────────────────────────
    // To tune: increase CIRCLE_GROWTH_RATE to expand faster, CIRCLE_LIFETIME for final size
    private static final float CIRCLE_DAMAGE      = 6f;
    private static final float CIRCLE_GROWTH_RATE = 0.35f;  // radius added per tick       ← SPEED/SIZE
    private static final int   CIRCLE_LIFETIME    = 10;     // ticks alive                 ← FINAL SIZE
    private static final int   CIRCLE_COUNT       = 12;     // number of wave entities
    private static final int   CIRCLE_COOLDOWN    = 40;

    private static final float CIRCLE_FORCE_BACK = 1;
    private static final float CIRCLE_FORCE_UP = 3;


    public WarriorsAxe(Properties props, Tier tier) {
        super(tier, props.attributes(createAttributes(tier, 8F, 0.4F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);


        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) spawnCircle(level, player);
            player.getCooldowns().addCooldown(this, CIRCLE_COOLDOWN);
        } else {
            if (!level.isClientSide()) spawnLine(level, player);
            player.getCooldowns().addCooldown(this, LINE_COOLDOWN);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Line shockwave: one wave that travels in the player's facing direction
    // ──────────────────────────────────────────────────────────────────────────
    private void spawnLine(Level level, Player player) {
        // Flatten the look vector so it stays on the ground plane
        Vec3 look = player.getLookAngle();
        Vec3 dir  = new Vec3(look.x, 0, look.z).normalize();

        // Start it 1.5 blocks in front of the player so it doesn't hit them
        Vec3 spawnPos = player.position()
                .add(dir.scale(1.5));

        ShockwaveEntity wave = ShockwaveEntity.create(
                level, player,
                spawnPos, dir,
                LINE_DAMAGE,
                LINE_SPEED,
                LINE_GROWTH_RATE,
                LINE_LIFETIME,
                LINE_FORCE_BACK,
                LINE_FORCE_UP,
                false
        );
        level.addFreshEntity(wave);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Circle burst: ring of short waves around the player
    // ──────────────────────────────────────────────────────────────────────────
    private void spawnCircle(Level level, Player player) {
        Vec3 centre = player.position();

        for (int i = 0; i < CIRCLE_COUNT; i++) {
            float angle = (float)(2.0 * Math.PI * i / CIRCLE_COUNT);
            Vec3 dir = new Vec3(Mth.cos(angle), 0, Mth.sin(angle));

            // Start each wave just outside the player so they don't self-hit
            Vec3 spawnPos = centre.add(dir.scale(1.2));

            ShockwaveEntity wave = ShockwaveEntity.create(
                    level, player,
                    spawnPos, dir,
                    CIRCLE_DAMAGE,
                    0f,                 // no forward movement — they expand in place
                    CIRCLE_GROWTH_RATE,
                    CIRCLE_LIFETIME,
                    CIRCLE_FORCE_BACK,
                    CIRCLE_FORCE_UP,
                    true
            );
            level.addFreshEntity(wave);
        }
    }
    public static @NotNull ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)(attackDamage + tier.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }
}

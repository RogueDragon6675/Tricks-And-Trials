package com.jackson.tricksandtrials.item.custom;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BoxingGlove extends Item {
    public static final ResourceLocation BASE_ATTACK_KNOCKBACK = ResourceLocation.fromNamespaceAndPath("tricksandtrials", "knockback");

    public static ItemAttributeModifiers createAttributes()
    {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) 3.4F, AttributeModifier.Operation.ADD_VALUE ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK, (double) 20.0F, AttributeModifier.Operation.ADD_VALUE ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double) 1000.0F, AttributeModifier.Operation.ADD_VALUE ), EquipmentSlotGroup.MAINHAND)
                .build();
    }
    public BoxingGlove(Properties properties) {
        super(properties.attributes(
                createAttributes()
        ));
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
       return !player.isCreative();
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        return super.onLeftClickEntity(stack, player, entity);
    }
}

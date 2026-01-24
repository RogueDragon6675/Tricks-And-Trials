package com.jackson.tricksandtrials.item.custom;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public class BoomMace extends SwordItem {

    public BoomMace(Tier tier,Properties properties){
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        if (!level.isClientSide()) {
            float boom = 10F;

            level.explode(attacker,null, null, target.getX(), target.getY(), target.getZ(), boom, true, Level.ExplosionInteraction.BLOCK);
        }
        stack.hurtAndBreak(10, attacker, EquipmentSlot.MAINHAND);
        return super.hurtEnemy(stack, target, attacker);
    }
}

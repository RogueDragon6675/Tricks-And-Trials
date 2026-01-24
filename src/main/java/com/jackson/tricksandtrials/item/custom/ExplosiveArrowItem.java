package com.jackson.tricksandtrials.item.custom;

import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.entity.custom.ExplosiveArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ExplosiveArrowItem extends ArrowItem {
    public ExplosiveArrowItem(Properties properties){
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {

        return new ExplosiveArrowEntity(level, shooter, ammo, weapon);
    }
}

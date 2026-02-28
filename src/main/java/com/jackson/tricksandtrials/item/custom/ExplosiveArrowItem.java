package com.jackson.tricksandtrials.item.custom;

import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.entity.custom.ExplosiveArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ExplosiveArrowItem extends ArrowItem {
    float radius;
    boolean createsfire;
    ItemLike item;
    public ExplosiveArrowItem(Properties properties, float radius, boolean createsfire){
        super(properties);
        this.radius = radius;
        this. createsfire = createsfire;
        this. item = getDefaultInstance().getItem();
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {

        ExplosiveArrowEntity arrow =  new ExplosiveArrowEntity(level, shooter, ammo, weapon);
        arrow.explosionRadius = radius;
        arrow.causefire = createsfire;
        arrow.item = getDefaultInstance();
        return arrow;
    }
}

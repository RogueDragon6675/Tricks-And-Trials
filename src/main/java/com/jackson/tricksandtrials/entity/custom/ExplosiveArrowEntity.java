package com.jackson.tricksandtrials.entity.custom;

import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.item.ModItems;
import com.jackson.tricksandtrials.item.custom.ExplosiveArrowItem;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ExplosiveArrowEntity extends AbstractArrow {
    public float explosionRadius = 5;
    public boolean causefire = true;
    public ItemStack item;
 public ExplosiveArrowEntity(EntityType<? extends ExplosiveArrowEntity> type, Level level){
     super(type, level);
 }
 public ExplosiveArrowEntity(Level level, LivingEntity shooter, ItemStack pickupStack, ItemStack weaponStack)
 {
     super(ModEntities.EXPLOSIVE_ARROW.get(), shooter, level, pickupStack, weaponStack);
 }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return item;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide)
        {
          this.createExplosion();
        }
    }
    private void createExplosion()
    {
        Entity owner = getOwner();
        this.level().explode(owner, this.getX(), this.getY(), this.getZ(), explosionRadius,causefire,
                Level.ExplosionInteraction.TNT);
        discard();
    }
}

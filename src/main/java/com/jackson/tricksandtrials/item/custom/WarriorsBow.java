package com.jackson.tricksandtrials.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.List;

public class WarriorsBow extends CrossbowItem {
    boolean useRailgun;
    public WarriorsBow(Properties properties) {
        super(properties);
    }
    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
    if (useRailgun)
    {
        shootRailgun(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
    }else {
        shootShotgun(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, target);
    }

    }
    public void toggleMode() {
    useRailgun =! useRailgun;
    }
    private void shootShotgun(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target)
    {
        float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
        float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for(int i = 0; i < projectileItems.size(); ++i) {
            ItemStack itemstack = (ItemStack) projectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float) ((i + 1) / 2) * f1;
                f3 = -f3;
                for (int j = 0; j < 5; ++j) {
                    Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
                    this.shootProjectile(shooter, projectile, i, velocity, inaccuracy * 10, f4, target);
                    level.addFreshEntity(projectile);
                }


                weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
    }
    private void shootRailgun(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target)
    {
        float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
        float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for(int i = 0; i < projectileItems.size(); ++i) {
            ItemStack itemstack = (ItemStack) projectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float) ((i + 1) / 2) * f1;
                f3 = -f3;
                for (int j = 0; j < 100; ++j) {
                    Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
                    this.shootProjectile(shooter, projectile, i, velocity, inaccuracy * 10, f4, target);
                    level.addFreshEntity(projectile);
                }


                weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }
    }

}

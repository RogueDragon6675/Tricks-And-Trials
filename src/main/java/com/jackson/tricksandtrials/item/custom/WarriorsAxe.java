package com.jackson.tricksandtrials.item.custom;

import com.jackson.tricksandtrials.TricksandTrials;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;


import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WarriorsAxe extends AxeItem {
    float shockwaveSpeed = 1.f;
    float shockwaveScaling = 0.4f;
    float shockwaveDamage = 6f;
    float circleShockwaveDuration = 0.8f;
    float lineShockwaveDuration = 30f;
    int numCircleProjectiles = 16;
    public WarriorsAxe(Properties p_40524_,Tier p_40521_ ) {
        super(p_40521_, p_40524_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
    if (player.isShiftKeyDown())
    {
    createCircleBoom(level,player);
    }else {
        createLineBoom(level,player);
    }


        return super.use(level, player, usedHand);
    }

    public void createCircleBoom(Level level, Player player)
    {
     TricksandTrials.LOGGER.info("DidCircleATTACK");
    }
    public void createLineBoom(Level level, Player player)
    {/*
        TricksandTrials.LOGGER.info("DidLineATTACK");
        Vec3 destination = player.getXRot()
        double d0 = Math.min(livingentity.getY(), Evoker.this.getY());
        double d1 = Math.max(livingentity.getY(), Evoker.this.getY()) + 1.0;
        float f = (float)Mth.atan2(livingentity.getZ() - Evoker.this.getZ(), livingentity.getX() - Evoker.this.getX());
        int k;
        for(k = 0; k < 16; ++k) {
            double d2 = 1.25 * (double)(k + 1);
            int j = 1 * k;
            this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(f) * d2, Evoker.this.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, j);
        } */
    }
    private void createBoomEntity(Vec3 direction, float lifetime)
    {

    }

}

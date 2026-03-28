package com.jackson.tricksandtrials.item.custom;

import com.jackson.tricksandtrials.TricksandTrials;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;


import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
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
    createCircleBoom();
    }else {
        createLineBoom();
    }


        return super.use(level, player, usedHand);
    }

    public void createCircleBoom()
    {
     TricksandTrials.LOGGER.info("DidCircleATTACK");
    }
    public void createLineBoom()
    {
        TricksandTrials.LOGGER.info("DidLineATTACK");
    }
    private void createBoomEntity(Vec3 direction, float lifetime)
    {

    }

}

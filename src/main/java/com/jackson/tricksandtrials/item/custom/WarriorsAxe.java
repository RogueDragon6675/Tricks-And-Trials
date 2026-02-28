package com.jackson.tricksandtrials.item.custom;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;

public class WarriorsAxe extends AxeItem {
    public WarriorsAxe(Properties p_40524_,Tier p_40521_ ) {
        super(p_40521_, p_40524_);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }
    public void createSonicBoom()
    {}
    
}

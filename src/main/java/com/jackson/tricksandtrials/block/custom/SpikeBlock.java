package com.jackson.tricksandtrials.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpikeBlock extends Block {
    private final SpikeTier tier;
    public SpikeBlock(Properties properties, SpikeTier tier){
        super(properties);
        this.tier = tier;
    }

    public SpikeTier getTier() {
         return tier;
    }

    // Damage multipliers for tiers

    private float getStepDamage(){
        return switch (tier) {
            case WOOD -> 0.5F;
            case STONE -> 1.0F;
            case GOLD -> 2.0F;
            case IRON ->  4.0F;
            case DIAMOND -> 7.0F;
            case NETHERITE -> 10.0F;
        };
    }

    private float getFallDamageExtra(){
        return switch (tier) {
            case WOOD -> 2.0F;
            case STONE -> 3.0F;
            case GOLD -> 6.0F;
            case IRON -> 8.0F;
            case DIAMOND -> 9.0F;
            case NETHERITE -> 12.0F;
        };
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        float extra = getFallDamageExtra();
        entity.causeFallDamage(fallDistance, extra, level.damageSources().stalagmite());
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        float step = getStepDamage();
        if(!entity.isSteppingCarefully() && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().cactus(), step);
        }
        super.stepOn(level, pos, state, entity);
    }
}

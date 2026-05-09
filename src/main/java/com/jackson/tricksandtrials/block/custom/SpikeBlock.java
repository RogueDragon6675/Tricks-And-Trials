package com.jackson.tricksandtrials.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpikeBlock extends Block {
    private final SpikeTier tier;
    public SpikeBlock(Properties properties, SpikeTier tier){
        super(properties);
        this.tier = tier;
    }

    @Override
    protected boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
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
        spawnHitParticles(level, pos, entity, 12);
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        float step = getStepDamage();
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().cactus(), step);
            spawnHitParticles(level, pos, entity, 1);
        }
        super.stepOn(level, pos, state, entity);
    }

    /** Spawns blood/crit particles at the entity's feet on the server so all clients see them. */
    private void spawnHitParticles(Level level, BlockPos pos, Entity entity, int count) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.0;
        double cz = pos.getZ() + 0.5;
        serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, cx, cy, cz,
                count, 0.3, 0.2, 0.3, 0.05);
        serverLevel.sendParticles(ParticleTypes.CRIT, cx, cy + 0.1, cz,
                count, 0.2, 0.15, 0.2, 0.1);
    }
}

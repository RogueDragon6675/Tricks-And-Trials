package com.jackson.tricksandtrials.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;

public class ShockwaveEntity extends EvokerFangs {
    public ShockwaveEntity(EntityType<? extends EvokerFangs> entityType, Level level) {
        super(entityType, level);
    }
}

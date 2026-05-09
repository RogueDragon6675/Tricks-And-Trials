package com.jackson.tricksandtrials.entity;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.entity.custom.ExplosiveArrowEntity;
import com.jackson.tricksandtrials.entity.custom.ShockwaveEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TricksandTrials.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ExplosiveArrowEntity>> EXPLOSIVE_ARROW =
            ENTITIES.register("explosive_arrow", () ->
                    EntityType.Builder
                            .<ExplosiveArrowEntity>of(ExplosiveArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("explosive_arrow")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<ShockwaveEntity>> SHOCKWAVE =
            ENTITIES.register("shockwave", () ->
                    EntityType.Builder
                            .<ShockwaveEntity>of(ShockwaveEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)   // actual hitbox is dynamic; this is just the initial placeholder
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("shockwave")
            );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}

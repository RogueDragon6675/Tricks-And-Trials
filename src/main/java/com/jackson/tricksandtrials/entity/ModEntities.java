package com.jackson.tricksandtrials.entity;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.entity.custom.ExplosiveArrowEntity;
import com.jackson.tricksandtrials.entity.custom.ShockwaveEntity;
import com.jackson.tricksandtrials.entity.custom.WarriorBossEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = TricksandTrials.MODID)
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
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("shockwave")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<WarriorBossEntity>> WARRIOR_BOSS =
            ENTITIES.register("warrior_boss", () ->
                    EntityType.Builder
                            .<WarriorBossEntity>of(WarriorBossEntity::new, MobCategory.MONSTER)
                            .sized(0.9f, 2.925f)
                            .clientTrackingRange(10)
                            .build("warrior_boss")
            );

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WARRIOR_BOSS.get(), WarriorBossEntity.createAttributes().build());
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
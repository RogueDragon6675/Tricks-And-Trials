package com.jackson.tricksandtrials.client;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.client.renderer.ExplosiveArrowRenderer;
import com.jackson.tricksandtrials.client.renderer.ShockwaveRenderer;
import com.jackson.tricksandtrials.client.renderer.WarriorBossRenderer;
import com.jackson.tricksandtrials.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.entity.EntityRenderers;

@EventBusSubscriber(modid = TricksandTrials.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
            EntityRenderers.register(ModEntities.SHOCKWAVE.get(), ShockwaveRenderer::new);
            EntityRenderers.register(ModEntities.WARRIOR_BOSS.get(), WarriorBossRenderer::new);
        });
    }
}
package com.jackson.tricksandtrials.client;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.client.renderer.ExplosiveArrowRenderer;
import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.entity.custom.ExplosiveArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = TricksandTrials.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
        });
    }
}

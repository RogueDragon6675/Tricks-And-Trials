package com.jackson.tricksandtrials.client.renderer;

import com.jackson.tricksandtrials.entity.custom.ShockwaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ShockwaveRenderer extends EntityRenderer<ShockwaveEntity> {

    public ShockwaveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShockwaveEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // All visuals are handled server-side via ServerLevel.sendParticles in ShockwaveEntity.tick()
    }

    @Override
    public ResourceLocation getTextureLocation(ShockwaveEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png");
    }
}

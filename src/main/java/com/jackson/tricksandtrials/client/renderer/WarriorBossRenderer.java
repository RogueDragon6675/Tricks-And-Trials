package com.jackson.tricksandtrials.client.renderer;

import com.jackson.tricksandtrials.entity.custom.WarriorBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

/**
 * PLACEHOLDER RENDERER — replace this when you have a custom model.
 *
 * HOW TO REPLACE WITH A CUSTOM MODEL:
 * ─────────────────────────────────────────────────────────────────────────────
 * 1. CREATE YOUR MODEL
 *    - Model your boss in Blockbench as a "Java Entity" project.
 *    - Export → "Export Java Entity" → save the .java file into:
 *        src/main/java/com/jackson/tricksandtrials/client/model/WarriorBossModel.java
 *    - Export the texture → save the .png into:
 *        src/main/resources/assets/tricksandtrials/textures/entity/warrior_boss.png
 *
 * 2. REGISTER THE LAYER DEFINITION
 *    - Blockbench also exports a createBodyLayer() method.
 *    - Create ModModelLayers.java and define a LayerDefinition constant:
 *        public static final ModelLayerLocation WARRIOR_BOSS =
 *            new ModelLayerLocation(
 *                ResourceLocation.fromNamespaceAndPath("tricksandtrials", "warrior_boss"), "main");
 *    - In a @EventBusSubscriber class on the MOD bus, subscribe to EntityRenderersEvent.RegisterLayerDefinitions:
 *        event.registerLayerDefinition(ModModelLayers.WARRIOR_BOSS, WarriorBossModel::createBodyLayer);
 *
 * 3. UPDATE THIS RENDERER
 *    - Change the generic type from HumanoidModel<WarriorBossEntity> to WarriorBossModel
 *    - Replace ModelLayers.PLAYER with ModModelLayers.WARRIOR_BOSS in the constructor
 *    - Replace new HumanoidModel<>(ctx.bakeLayer(...)) with new WarriorBossModel(ctx.bakeLayer(...))
 *    - Update TEXTURE to point to your new texture file
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class WarriorBossRenderer extends MobRenderer<WarriorBossEntity, HumanoidModel<WarriorBossEntity>> {

    // ↓ Replace this with your actual texture path when ready
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("tricksandtrials", "textures/entity/warrior_boss.png");

    public WarriorBossRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(WarriorBossEntity entity) {
        return TEXTURE;
    }
}
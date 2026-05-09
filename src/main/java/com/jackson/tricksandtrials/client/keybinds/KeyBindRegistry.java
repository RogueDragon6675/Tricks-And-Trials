package com.jackson.tricksandtrials.client.keybinds;

import com.jackson.tricksandtrials.TricksandTrials;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = TricksandTrials.MODID, value = Dist.CLIENT)
public class KeyBindRegistry {
    //public static final KeyMapping.Category shrek = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(TricksandTrials.MODID,"category"));
    public static final KeyMapping BEGINSHREKING = new KeyMapping(
            "key.tricksandtrials.bow",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.category.tricksandtrials"
    );
    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent Shrek)
    {
        Shrek.register(BEGINSHREKING);
    }
}

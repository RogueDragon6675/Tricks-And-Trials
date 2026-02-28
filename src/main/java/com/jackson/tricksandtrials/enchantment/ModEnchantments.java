package com.jackson.tricksandtrials.enchantment;

import com.jackson.tricksandtrials.TricksandTrials;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> QUICK_SHOT = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(TricksandTrials.MODID, "quick_shot"));

public static final ResourceKey<Enchantment> HOMING = ResourceKey.create(Registries.ENCHANTMENT,
        ResourceLocation.fromNamespaceAndPath(TricksandTrials.MODID,"homing"));



    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
                                 Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}

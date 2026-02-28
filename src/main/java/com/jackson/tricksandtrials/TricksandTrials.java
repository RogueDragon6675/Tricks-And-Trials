package com.jackson.tricksandtrials;

import com.jackson.tricksandtrials.block.ModBlocks;
import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.item.ModItems;
import com.jackson.tricksandtrials.item.custom.ExplosiveArrowItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TricksandTrials.MODID)
public class TricksandTrials {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tricksandtrials";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TricksandTrials(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT){
            event.accept(ModItems.BOXINGGLOVE);
            event.accept(ModItems.EXPLOSIVE_ARROW);
            event.accept(ModItems.STOPWATCH);
            event.accept(ModItems.BOOMMACE);
            event.accept(ModItems.NUKE);
            event.accept(ModItems.WARRIORS_AXE);
            event.accept(ModItems.WARRIORS_BOW);
        }
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS)
        {
            event.accept(ModBlocks.WOOD_SPIKE_BLOCK);
            event.accept(ModBlocks.STONE_SPIKE_BLOCK);
            event.accept(ModBlocks.GOLD_SPIKE_BLOCK);
            event.accept(ModBlocks.IRON_SPIKE_BLOCK);
            event.accept(ModBlocks.DIAMOND_SPIKE_BLOCK);
            event.accept(ModBlocks.NETHERITE_SPIKE_BLOCK);
        }

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}

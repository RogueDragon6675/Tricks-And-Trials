package com.jackson.tricksandtrials;

import com.jackson.tricksandtrials.block.ModBlocks;
import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.item.ModDataComponents;
import com.jackson.tricksandtrials.item.ModItems;
import com.jackson.tricksandtrials.worldgen.ModStructures;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(TricksandTrials.MODID)
public class TricksandTrials {
    public static final String MODID = "tricksandtrials";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TricksandTrials(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModStructures.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.BOXINGGLOVE);
            event.accept(ModItems.EXPLOSIVE_ARROW);
            event.accept(ModItems.STOPWATCH);
            event.accept(ModItems.BOOMMACE);
            event.accept(ModItems.NUKE);
            event.accept(ModItems.WARRIORS_AXE);
            event.accept(ModItems.WARRIORS_BOW);
            event.accept(ModItems.WARRIOR_BOSS_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModBlocks.WOOD_SPIKE_BLOCK);
            event.accept(ModBlocks.STONE_SPIKE_BLOCK);
            event.accept(ModBlocks.GOLD_SPIKE_BLOCK);
            event.accept(ModBlocks.IRON_SPIKE_BLOCK);
            event.accept(ModBlocks.DIAMOND_SPIKE_BLOCK);
            event.accept(ModBlocks.NETHERITE_SPIKE_BLOCK);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
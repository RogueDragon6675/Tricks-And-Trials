package com.jackson.tricksandtrials.item;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.entity.ModEntities;
import com.jackson.tricksandtrials.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TricksandTrials.MODID);

    public static final DeferredItem<Item> BOXINGGLOVE = ITEMS.register("boxingglove",
            () -> new BoxingGlove(new Item.Properties().durability(32)));

    public static final DeferredItem<Item> BOOMMACE = ITEMS.register("boommace",
            () -> new BoomMace(ModTiers.KAHBOOMMACE, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> STOPWATCH = ITEMS.register("stopwatch",
            () -> new StopWatch(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> EXPLOSIVE_ARROW = ITEMS.register("explosive_arrow",
            () -> new ExplosiveArrowItem(new Item.Properties().stacksTo(16), 5, true));

    public static final DeferredItem<Item> NUKE = ITEMS.register("nuke",
            () -> new ExplosiveArrowItem(new Item.Properties().stacksTo(3), 100, true));

    public static final DeferredItem<Item> WARRIORS_AXE = ITEMS.register("warriorsaxe",
            () -> new WarriorsAxe(new Item.Properties().stacksTo(1), Tiers.NETHERITE));

    public static final DeferredItem<Item> WARRIORS_BOW = ITEMS.register("warriorsbow",
            () -> new WarriorsBow(new Item.Properties().stacksTo(1)));

    // 0x2C2C2C = dark grey body, 0xB22222 = firebrick red spots
    public static final DeferredItem<Item> WARRIOR_BOSS_SPAWN_EGG = ITEMS.register("warrior_boss_spawn_egg",
            () -> new SpawnEggItem(ModEntities.WARRIOR_BOSS.get(), 0x2C2C2C, 0xB22222,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
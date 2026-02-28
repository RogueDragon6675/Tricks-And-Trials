package com.jackson.tricksandtrials.item;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.item.custom.BoomMace;
import com.jackson.tricksandtrials.item.custom.BoxingGlove;
import com.jackson.tricksandtrials.item.custom.ExplosiveArrowItem;
import com.jackson.tricksandtrials.item.custom.StopWatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.jackson.tricksandtrials.item.ModTiers;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TricksandTrials.MODID);

    public static final DeferredItem<Item> BOXINGGLOVE = ITEMS.register("boxingglove", () -> new BoxingGlove(new Item.Properties().durability(32)));

    public static final DeferredItem<Item> BOOMMACE = ITEMS.register("boommace", () -> new BoomMace(ModTiers.KAHBOOMMACE, new Item.Properties().stacksTo(1)));

public static final DeferredItem<Item> STOPWATCH = ITEMS.register("stopwatch", () -> new StopWatch (new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> EXPLOSIVE_ARROW = ITEMS.register("explosive_arrow",
            () -> new ExplosiveArrowItem(new Item.Properties().stacksTo(16),5,true));

    public static final DeferredItem<Item> NUKE = ITEMS.register("nuke",
            () -> new ExplosiveArrowItem(new Item.Properties().stacksTo(3),100,true));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

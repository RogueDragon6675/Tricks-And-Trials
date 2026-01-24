package com.jackson.tricksandtrials.block;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.block.custom.SpikeBlock;
import com.jackson.tricksandtrials.block.custom.SpikeTier;
import com.jackson.tricksandtrials.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TricksandTrials.MODID);

    public static final DeferredBlock<Block> WOOD_SPIKE_BLOCK = registerBlock("wood_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.WOOD));
    public static final DeferredBlock<Block> STONE_SPIKE_BLOCK = registerBlock("stone_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.STONE));
    public static final DeferredBlock<Block> IRON_SPIKE_BLOCK = registerBlock("iron_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.IRON));
    public static final DeferredBlock<Block> GOLD_SPIKE_BLOCK = registerBlock("gold_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.GOLD));
    public static final DeferredBlock<Block> DIAMOND_SPIKE_BLOCK = registerBlock("diamond_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.DIAMOND));
    public static final DeferredBlock<Block> NETHERITE_SPIKE_BLOCK = registerBlock("netherite_spike_block", () -> new SpikeBlock(BlockBehaviour.Properties.of()
            .strength(4f).requiresCorrectToolForDrops(), SpikeTier.NETHERITE));
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
    {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block)
    {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}

package com.jackson.tricksandtrials.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;


import java.util.function.Supplier;

public class ModTiers{
    public static final Tier KAHBOOMMACE = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 300, 1F, 50, 0, () -> Ingredient.of(Items.TNT) );

}


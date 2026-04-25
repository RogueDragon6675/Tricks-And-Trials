package com.jackson.tricksandtrials.worldgen;

import com.jackson.tricksandtrials.TricksandTrials;
import com.jackson.tricksandtrials.worldgen.custom.BossBattleStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, TricksandTrials.MODID);

    public static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
    }

    public static final DeferredHolder<StructureType<?>, StructureType<BossBattleStructure>>   BOSS_BATTLE_STRUCTURE = STRUCTURE_TYPES.register("boss_battle",() -> () -> BossBattleStructure.CODEC);
}

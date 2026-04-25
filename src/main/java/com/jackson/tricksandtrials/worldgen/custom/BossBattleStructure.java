package com.jackson.tricksandtrials.worldgen.custom;

import com.jackson.tricksandtrials.worldgen.ModStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

public class BossBattleStructure extends Structure {
    public static final MapCodec<BossBattleStructure> CODEC = Structure.simpleCodec(BossBattleStructure::new);
    protected BossBattleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext Context) {
        ChunkPos chunkPos = Context.chunkPos();
        int y = Context.chunkGenerator().getFirstOccupiedHeight(chunkPos.getMiddleBlockX(), chunkPos.getMiddleBlockZ(), Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Context.heightAccessor(),Context.randomState() );
        BlockPos spawnPos = new BlockPos(chunkPos.getMiddleBlockX(),y + 6, chunkPos.getMiddleBlockZ());
        Holder<StructureTemplatePool>pool = Context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL).getHolderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("tricksandtrials","boss_battle_pool")));
        return JigsawPlacement.addPieces(Context, pool, Optional.empty(),7, spawnPos, false, Optional.empty(), 80, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.BOSS_BATTLE_STRUCTURE.get();
    }
}

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

public class VillageStructure extends Structure {
    public static final MapCodec<VillageStructure> CODEC = Structure.simpleCodec(VillageStructure::new);

    protected VillageStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int y = context.chunkGenerator().getFirstOccupiedHeight(
                chunkPos.getMiddleBlockX(),
                chunkPos.getMiddleBlockZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );
        BlockPos spawnPos = new BlockPos(chunkPos.getMiddleBlockX(), y, chunkPos.getMiddleBlockZ());

        Holder<StructureTemplatePool> pool = context.registryAccess()
                .registryOrThrow(Registries.TEMPLATE_POOL)
                .getHolderOrThrow(ResourceKey.create(
                        Registries.TEMPLATE_POOL,
                        ResourceLocation.fromNamespaceAndPath("tricksandtrials", "village/start_pool")
                ));

        // size 6 = how many jigsaw pieces deep it will try to place (controls village spread)
        return JigsawPlacement.addPieces(
                context,
                pool,
                Optional.empty(),
                6,
                spawnPos,
                false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                80,
                PoolAliasLookup.EMPTY,
                DimensionPadding.ZERO,
                LiquidSettings.IGNORE_WATERLOGGING
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.VILLAGE_STRUCTURE.get();
    }
}

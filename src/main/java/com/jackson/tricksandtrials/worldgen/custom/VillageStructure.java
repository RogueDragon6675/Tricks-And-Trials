package com.jackson.tricksandtrials.worldgen.custom;

import com.jackson.tricksandtrials.worldgen.ModStructures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

public class VillageStructure extends Structure {

    // A custom codec (not simpleCodec) so we can read EXTRA fields from the
    // structure JSON beyond the shared StructureSettings -- specifically:
    //   start_pool, size, max_distance_from_center, and pool_aliases.
    // simpleCodec would ONLY parse the shared settings and silently drop these,
    // which is why pool_aliases must be parsed here explicitly.
    public static final MapCodec<VillageStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    // The shared structure settings (biomes, step, terrain_adaptation, etc.)
                    Structure.settingsCodec(instance),
                    // start_pool: the first jigsaw pool (the village center).
                    StructureTemplatePool.CODEC.fieldOf("start_pool")
                            .forGetter(s -> s.startPool),
                    // size: max jigsaw branching depth.
                    com.mojang.serialization.Codec.intRange(0, 30).fieldOf("size")
                            .forGetter(s -> s.size),
                    // max_distance_from_center: hard radius cap, in blocks.
                    com.mojang.serialization.Codec.intRange(1, 128)
                            .fieldOf("max_distance_from_center")
                            .forGetter(s -> s.maxDistanceFromCenter),
                    // pool_aliases: redirect rules (vanilla road -> our houses).
                    // Optional so the JSON can omit it; defaults to empty list.
                    PoolAliasBinding.CODEC.listOf()
                            .optionalFieldOf("pool_aliases", List.of())
                            .forGetter(s -> s.poolAliases)
            ).apply(instance, VillageStructure::new)
    );

    private final Holder<StructureTemplatePool> startPool;
    private final int size;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;

    protected VillageStructure(StructureSettings settings,
                               Holder<StructureTemplatePool> startPool,
                               int size,
                               int maxDistanceFromCenter,
                               List<PoolAliasBinding> poolAliases) {
        super(settings);
        this.startPool = startPool;
        this.size = size;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();

        // Find the surface height at the middle of this chunk so the village
        // sits on the ground.
        int y = context.chunkGenerator().getFirstOccupiedHeight(
                chunkPos.getMiddleBlockX(),
                chunkPos.getMiddleBlockZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );
        BlockPos spawnPos = new BlockPos(chunkPos.getMiddleBlockX(), y, chunkPos.getMiddleBlockZ());

        // Build the alias lookup from the parsed JSON rules. This is what makes
        // vanilla street jigsaws pull OUR houses pool instead of vanilla houses.
        PoolAliasLookup aliasLookup = PoolAliasLookup.create(
                this.poolAliases, spawnPos, context.seed());

        return JigsawPlacement.addPieces(
                context,
                this.startPool,                              // start pool from JSON
                Optional.empty(),                            // no forced starting jigsaw name
                this.size,                                   // branching depth from JSON
                spawnPos,
                false,                                       // no legacy expansion hack
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), // snap pieces to surface
                this.maxDistanceFromCenter,                  // radius cap from JSON
                aliasLookup,                                 // <-- the redirect rules
                DimensionPadding.ZERO,
                LiquidSettings.IGNORE_WATERLOGGING
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.VILLAGE_STRUCTURE.get();
    }
}
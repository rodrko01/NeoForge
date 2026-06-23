package net.rodrko.mod.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rodrko.mod.StinkyMod;
import net.rodrko.mod.block.ModBlocks;
import net.rodrko.mod.block.custom.GrowthChamberBlock;
import net.rodrko.mod.block.custom.PedestalBlock;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, StinkyMod.MOD_ID);


    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BE =
            BLOCK_ENTITIES.register("pedestal_be", () -> BlockEntityType.Builder.of(
                    PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build(null));

    public static final Supplier<BlockEntityType<GrowthChamberBlockEntity>> GROWTH_CHAMBER_BE =
            BLOCK_ENTITIES.register("growth_chamber_be", () -> BlockEntityType.Builder.of(
                    GrowthChamberBlockEntity::new, ModBlocks.GROWTH_CHAMBER.get()).build(null));

    public static final Supplier<BlockEntityType<TallBlockEntity>> TALL_BLOCK_BE =
            BLOCK_ENTITIES.register("tall_block_be", () -> BlockEntityType.Builder.of(
                    TallBlockEntity::new, ModBlocks.TALL_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

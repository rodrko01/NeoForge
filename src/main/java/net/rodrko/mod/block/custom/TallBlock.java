package net.rodrko.mod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rodrko.mod.block.entity.GrowthChamberBlockEntity;
import net.rodrko.mod.block.entity.ModBlockEntities;
import net.rodrko.mod.block.entity.TallBlockEntity;
import net.rodrko.mod.screen.custom.TallBlockMenu;
import org.jetbrains.annotations.Nullable;

public class TallBlock extends BaseEntityBlock {
    // Properties
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final MapCodec<TallBlock> CODEC = simpleCodec(TallBlock::new);

    public static final BooleanProperty CRAFTING = BooleanProperty.create("crafting");

    public TallBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, Direction.NORTH).setValue(CRAFTING, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            // if click up half, get lower half entity
            BlockPos targetPos = pPos;
            if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
                targetPos = pPos.below();
            }
            BlockEntity entity = pLevel.getBlockEntity(targetPos);
            if(entity instanceof TallBlockEntity tallBlockEntity) {
                // add blockpos into packet
                ((ServerPlayer)pPlayer).openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) ->
                                new TallBlockMenu(containerId, playerInventory, tallBlockEntity, tallBlockEntity.getData()),
                        Component.literal("tall block")
                ), targetPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            // drop items from lower half only
            if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof TallBlockEntity tallBlockEntity) {
                    tallBlockEntity.drops();
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        level.playSound(player, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1f, 1f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();

        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, context.getHorizontalDirection().getOpposite());
            // comments are for stinkers
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // Automatically place the UPPER half above the placed block
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return super.getShape(state, level, pos, context);

    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        // If the other half is broken, break this half too
        if (direction.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER == (direction == Direction.UP))) {
            if (!neighborState.is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockstate = level.getBlockState(pos.below());
            return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Register all properties to the builder
        builder.add(HALF, FACING, CRAFTING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        // tick the lower half
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return createTickerHelper(blockEntityType, ModBlockEntities.TALL_BLOCK_BE.get(),
                    (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
        }

        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TallBlockEntity(pos, state);
    }
}

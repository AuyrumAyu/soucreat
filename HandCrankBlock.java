package com.mechanica;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HandCrankBlock extends KineticBlock {
    public static final DirectionProperty FACING = Properties.FACING;

    private static final VoxelShape UP = Block.createCuboidShape(5, 0, 5, 11, 12, 11);
    private static final VoxelShape DOWN = Block.createCuboidShape(5, 4, 5, 11, 16, 11);
    private static final VoxelShape NORTH = Block.createCuboidShape(5, 5, 4, 11, 11, 16);
    private static final VoxelShape SOUTH = Block.createCuboidShape(5, 5, 0, 11, 11, 12);
    private static final VoxelShape EAST = Block.createCuboidShape(0, 5, 5, 12, 11, 11);
    private static final VoxelShape WEST = Block.createCuboidShape(4, 5, 5, 16, 11, 11);

    public HandCrankBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public boolean isSource() { return true; }

    @Override
    public boolean emits(BlockState state, Direction d) { return true; }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof KineticBlockEntity be) {
            be.poweredUntil = world.getTime() + 40;
            be.markDirty();
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.6f, 1.2f);
        }
        return ActionResult.success(world.isClient);
    }
}

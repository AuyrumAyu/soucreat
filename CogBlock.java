package com.mechanica;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CogBlock extends AxisKineticBlock {
    private static final VoxelShape X = Block.createCuboidShape(6, 0, 0, 10, 16, 16);
    private static final VoxelShape Y = Block.createCuboidShape(0, 6, 0, 16, 10, 16);
    private static final VoxelShape Z = Block.createCuboidShape(0, 0, 6, 16, 16, 10);

    public CogBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isCog() { return true; }

    @Override
    public Direction.Axis cogAxis(BlockState state) { return state.get(AXIS); }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }
}

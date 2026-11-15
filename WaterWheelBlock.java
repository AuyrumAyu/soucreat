package com.mechanica;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class WaterWheelBlock extends AxisKineticBlock {
    private static final VoxelShape X = Block.createCuboidShape(5, 0, 0, 11, 16, 16);
    private static final VoxelShape Y = Block.createCuboidShape(0, 5, 0, 16, 11, 16);
    private static final VoxelShape Z = Block.createCuboidShape(0, 0, 5, 16, 16, 11);

    public WaterWheelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSource() { return true; }

    @Override
    public boolean accepts(BlockState state, net.minecraft.util.math.Direction from) { return false; }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }
}

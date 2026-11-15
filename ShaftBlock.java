package com.mechanica;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ShaftBlock extends AxisKineticBlock {
    private static final VoxelShape X = Block.createCuboidShape(0, 6, 6, 16, 10, 10);
    private static final VoxelShape Y = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
    private static final VoxelShape Z = Block.createCuboidShape(6, 6, 0, 10, 10, 16);

    public ShaftBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(AXIS)) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }
}

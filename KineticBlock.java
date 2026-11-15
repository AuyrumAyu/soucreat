package com.mechanica;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** Base class of everything that takes part in the rotation network. */
public abstract class KineticBlock extends BlockWithEntity {
    protected KineticBlock(Settings settings) {
        super(settings);
    }

    /** Does this block pass rotation on through the face in direction d? */
    public boolean emits(BlockState state, Direction d) { return false; }

    /** Does this block take rotation in through the face that looks at direction 'from'? */
    public boolean accepts(BlockState state, Direction from) { return false; }

    /** Sources (crank, water wheel) start the rotation. */
    public boolean isSource() { return false; }

    public boolean isCog() { return false; }

    public Direction.Axis cogAxis(BlockState state) { return null; }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KineticBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Mechanica.KINETIC_BE, KineticBlockEntity::tick);
    }
}

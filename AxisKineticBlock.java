package com.mechanica;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

/** Kinetic block that sits on an axis (shaft, cogwheel, water wheel). */
public abstract class AxisKineticBlock extends KineticBlock {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    protected AxisKineticBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public boolean emits(BlockState state, Direction d) {
        return d.getAxis() == state.get(AXIS);
    }

    @Override
    public boolean accepts(BlockState state, Direction from) {
        return from.getAxis() == state.get(AXIS);
    }
}

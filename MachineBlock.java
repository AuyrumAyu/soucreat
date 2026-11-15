package com.mechanica;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

/** Press and crusher: take rotation from any side, work on items in the cell below. */
public class MachineBlock extends KineticBlock {
    public MachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean accepts(BlockState state, Direction from) { return true; }
}

package com.mechanica;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

/** Draws the moving parts (spinning shafts, hammering press head...) of every machine. */
public class KineticRenderer implements BlockEntityRenderer<KineticBlockEntity> {
    private final BlockRenderManager blocks;

    public KineticRenderer(BlockEntityRendererFactory.Context ctx) {
        this.blocks = ctx.getRenderManager();
    }

    @Override
    public void render(KineticBlockEntity be, float tickDelta, MatrixStack m, VertexConsumerProvider vcp, int light, int overlay) {
        BlockState state = be.getCachedState();
        Block b = state.getBlock();
        float angle = be.angle + be.speed * 0.3f * tickDelta;
        BlockPos pos = be.getPos();

        m.push();
        m.translate(0.5, 0.5, 0.5);

        if (b == Mechanica.SHAFT || b == Mechanica.COGWHEEL || b == Mechanica.WATER_WHEEL) {
            Direction.Axis axis = state.get(Properties.AXIS);
            alignToAxis(m, axis);
            float offset = 0f;
            if (b == Mechanica.COGWHEEL && ((pos.getX() + pos.getY() + pos.getZ()) & 1) == 1) offset = 22.5f;
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle + offset));
            m.translate(-0.5, -0.5, -0.5);
            Block rotor = b == Mechanica.SHAFT ? Mechanica.SHAFT_ROTOR
                    : b == Mechanica.COGWHEEL ? Mechanica.COG_ROTOR : Mechanica.WHEEL_ROTOR;
            blocks.renderBlockAsEntity(rotor.getDefaultState(), m, vcp, light, overlay);

        } else if (b == Mechanica.HAND_CRANK) {
            orientUpTo(m, state.get(Properties.FACING));
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
            m.translate(-0.5, -0.5, -0.5);
            blocks.renderBlockAsEntity(Mechanica.CRANK_ROTOR.getDefaultState(), m, vcp, light, overlay);

        } else if (b == Mechanica.PRESS) {
            float depth = 0f;
            if (be.speed != 0f && be.getWorld() != null) {
                float t = ((be.getWorld().getTime() % 10) + tickDelta) / 10f;
                depth = t < 0.5f ? (t * 2f) * (t * 2f) : 2f - 2f * t;
            }
            m.translate(-0.5, -0.5 - 0.7 * depth, -0.5);
            blocks.renderBlockAsEntity(Mechanica.PRESS_HEAD.getDefaultState(), m, vcp, light, overlay);

        } else if (b == Mechanica.CRUSHER) {
            m.translate(0, -0.15, 0);
            m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
            m.translate(-0.5, -0.5, -0.5);
            blocks.renderBlockAsEntity(Mechanica.CRUSHER_ROTOR.getDefaultState(), m, vcp, light, overlay);
        }

        m.pop();
    }

    /** Rotor models are built along the Y axis; turn them to lie along the block's axis. */
    private static void alignToAxis(MatrixStack m, Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
        } else if (axis == Direction.Axis.Z) {
            m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        }
    }

    /** Crank model points up; turn it so it points in the given direction. */
    private static void orientUpTo(MatrixStack m, Direction d) {
        switch (d) {
            case DOWN -> m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            case NORTH -> m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            case SOUTH -> m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            case EAST -> m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90));
            case WEST -> m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
            default -> { }
        }
    }
}

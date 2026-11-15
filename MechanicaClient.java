package com.mechanica;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MechanicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(Mechanica.KINETIC_BE, KineticRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                Mechanica.SHAFT, Mechanica.COGWHEEL, Mechanica.HAND_CRANK, Mechanica.WATER_WHEEL,
                Mechanica.PRESS, Mechanica.CRUSHER, Mechanica.BELT,
                Mechanica.SHAFT_ROTOR, Mechanica.COG_ROTOR, Mechanica.CRANK_ROTOR, Mechanica.WHEEL_ROTOR,
                Mechanica.PRESS_HEAD, Mechanica.CRUSHER_ROTOR);
    }
}

package com.mechanica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Mechanica implements ModInitializer {
    public static final String ID = "mechanica";

    // ---- machine blocks (have items) ----
    public static final Block SHAFT = block("shaft", new ShaftBlock(metal()));
    public static final Block COGWHEEL = block("cogwheel", new CogBlock(wood()));
    public static final Block HAND_CRANK = block("hand_crank", new HandCrankBlock(wood()));
    public static final Block WATER_WHEEL = block("water_wheel", new WaterWheelBlock(wood()));
    public static final Block PRESS = block("press", new MachineBlock(metal()));
    public static final Block CRUSHER = block("crusher", new MachineBlock(metal()));
    public static final Block BELT = block("conveyor_belt", new BeltBlock(metal()));

    // ---- invisible "parts" that the block entity renderer spins/moves (never placed, no items) ----
    public static final Block SHAFT_ROTOR = part("shaft_rotor");
    public static final Block COG_ROTOR = part("cog_rotor");
    public static final Block CRANK_ROTOR = part("crank_rotor");
    public static final Block WHEEL_ROTOR = part("wheel_rotor");
    public static final Block PRESS_HEAD = part("press_head");
    public static final Block CRUSHER_ROTOR = part("crusher_rotor");

    public static final BlockEntityType<KineticBlockEntity> KINETIC_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, new Identifier(ID, "kinetic"),
            FabricBlockEntityTypeBuilder.create(KineticBlockEntity::new,
                    SHAFT, COGWHEEL, HAND_CRANK, WATER_WHEEL, PRESS, CRUSHER, BELT).build());

    // ---- items ----
    public static final Item IRON_PLATE = item("iron_plate");
    public static final Item GOLD_PLATE = item("gold_plate");
    public static final Item COPPER_PLATE = item("copper_plate");

    private static FabricBlockSettings wood() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque().strength(2.0f);
    }

    private static FabricBlockSettings metal() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque().strength(3.0f).sounds(BlockSoundGroup.METAL);
    }

    private static <T extends Block> T block(String name, T block) {
        Registry.register(Registries.BLOCK, new Identifier(ID, name), block);
        Registry.register(Registries.ITEM, new Identifier(ID, name), new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    private static Block part(String name) {
        Block b = new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque());
        return Registry.register(Registries.BLOCK, new Identifier(ID, name), b);
    }

    private static Item item(String name) {
        return Registry.register(Registries.ITEM, new Identifier(ID, name), new Item(new FabricItemSettings()));
    }

    @Override
    public void onInitialize() {
        Recipes.init();

        Registry.register(Registries.ITEM_GROUP, new Identifier(ID, "main"), FabricItemGroup.builder()
                .icon(() -> new ItemStack(PRESS))
                .displayName(Text.translatable("itemGroup.mechanica.main"))
                .entries((ctx, entries) -> {
                    entries.add(HAND_CRANK);
                    entries.add(WATER_WHEEL);
                    entries.add(SHAFT);
                    entries.add(COGWHEEL);
                    entries.add(PRESS);
                    entries.add(CRUSHER);
                    entries.add(BELT);
                    entries.add(IRON_PLATE);
                    entries.add(GOLD_PLATE);
                    entries.add(COPPER_PLATE);
                })
                .build());
    }
}

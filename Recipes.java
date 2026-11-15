package com.mechanica;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Machine "recipes": what the press and the crusher turn an item into. */
public final class Recipes {
    public static final Map<Item, ItemStack> PRESS = new HashMap<>();
    public static final Map<Item, ItemStack> CRUSH = new HashMap<>();

    private Recipes() {}

    public static void init() {
        PRESS.put(Items.IRON_INGOT, new ItemStack(Mechanica.IRON_PLATE));
        PRESS.put(Items.GOLD_INGOT, new ItemStack(Mechanica.GOLD_PLATE));
        PRESS.put(Items.COPPER_INGOT, new ItemStack(Mechanica.COPPER_PLATE));
        PRESS.put(Items.SUGAR_CANE, new ItemStack(Items.PAPER));

        CRUSH.put(Items.STONE, new ItemStack(Items.COBBLESTONE));
        CRUSH.put(Items.COBBLESTONE, new ItemStack(Items.GRAVEL));
        CRUSH.put(Items.GRAVEL, new ItemStack(Items.SAND));
        CRUSH.put(Items.IRON_ORE, new ItemStack(Items.RAW_IRON, 2));
        CRUSH.put(Items.DEEPSLATE_IRON_ORE, new ItemStack(Items.RAW_IRON, 2));
        CRUSH.put(Items.GOLD_ORE, new ItemStack(Items.RAW_GOLD, 2));
        CRUSH.put(Items.DEEPSLATE_GOLD_ORE, new ItemStack(Items.RAW_GOLD, 2));
        CRUSH.put(Items.COPPER_ORE, new ItemStack(Items.RAW_COPPER, 3));
        CRUSH.put(Items.DEEPSLATE_COPPER_ORE, new ItemStack(Items.RAW_COPPER, 3));
        CRUSH.put(Items.COAL_ORE, new ItemStack(Items.COAL, 2));
        CRUSH.put(Items.DEEPSLATE_COAL_ORE, new ItemStack(Items.COAL, 2));
        CRUSH.put(Items.BONE, new ItemStack(Items.BONE_MEAL, 5));
    }
}

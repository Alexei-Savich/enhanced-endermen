package com.asavich.enhancedendermen.creative;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static com.asavich.enhancedendermen.init.ItemInit.RED_ENDER_PEARL;

public class ModCreativeTab extends CreativeModeTab {

    public static final ModCreativeTab instance = new ModCreativeTab(CreativeModeTab.TABS.length, "enhancedendermen");


    public ModCreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RED_ENDER_PEARL.get());
    }
}

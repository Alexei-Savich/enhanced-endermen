package com.asavich.enhancedendermen.init.tiers;

import com.asavich.enhancedendermen.init.ItemInit;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

public class ItemTiers {
    public static final ForgeTier WAND_OF_TP_TIER = new ForgeTier(
            4,
            500,
            0.3f,
            0,
            20,
            Tags.Blocks.NEEDS_WOOD_TOOL,
            () -> Ingredient.of(ItemInit.WAND_OF_TELEPORTATION::get)
    );

    public static final ForgeTier SWORD_OF_TP_TIER = new ForgeTier(
            4,
            1000,
            0.5f,
            0,
            20,
            Tags.Blocks.NEEDS_WOOD_TOOL,
            () -> Ingredient.of(ItemInit.SWORD_OF_TELEPORTATION::get)
    );
}

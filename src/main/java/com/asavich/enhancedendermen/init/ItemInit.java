package com.asavich.enhancedendermen.init;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.entities.RedWanderer;
import com.asavich.enhancedendermen.items.TeleportationArrowItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnhancedEndermen.MOD_ID);

    public static final RegistryObject<Item> RED_ENDER_PEARL = CreativeTabInit.addToTab(ITEMS.register("red_ender_pearl",
            () -> new Item(new Item.Properties().stacksTo(16))));

    public static final RegistryObject<Item> RED_EYE_OF_ENDER = CreativeTabInit.addToTab(ITEMS.register("red_eye_of_ender",
            () -> new Item(new Item.Properties().stacksTo(16))));

    public static final RegistryObject<Item> RED_ENDER_SHARD = CreativeTabInit.addToTab(ITEMS.register("red_ender_shard",
                    () -> new Item(new Item.Properties()
                            .stacksTo(64)
                            .food(new FoodProperties.Builder()
                                    .nutrition(1)
                                    .saturationMod(0.1f)
                                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 20000, 0), 0.05f)
                                    .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 200, 0), 0.05f)
                                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 0.10f)
                                    .alwaysEat()
                                    .fast()
                                    .build()))
            )
    );

    public static final RegistryObject<Item> TELEPORTATION_ARROW = CreativeTabInit.addToTab(ITEMS.register("teleportation_arrow",
            () -> new TeleportationArrowItem(new Item.Properties().stacksTo(64))));


    public static final RegistryObject<Item> COOKED_RED_ENDER_SHARD = CreativeTabInit.addToTab(ITEMS.register("cooked_red_ender_shard",
                    () -> new Item(new Item.Properties()
                            .stacksTo(64)
                            .food(new FoodProperties.Builder()
                                    .nutrition(1)
                                    .saturationMod(0.4f)
                                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 10000, 0), 0.10f)
                                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 4), 0.01f)
                                    .alwaysEat()
                                    .fast()
                                    .build()))
            )
    );

    public static final RegistryObject<ForgeSpawnEggItem> RED_WANDERER_SPAWN_EGG = CreativeTabInit.addToTab(ITEMS.register("red_wanderer_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityInit.RED_WANDERER, 0x700000, 0x000000, new Item.Properties())
    ));

    public static final RegistryObject<BlockItem> REPELLER = CreativeTabInit.addToTab(ITEMS.register("repeller",
            () -> new BlockItem(BlockInit.REPELLER.get(), new Item.Properties().stacksTo(64))
    ));
}
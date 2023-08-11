package com.asavich.enhancedendermen.init;

import com.asavich.enhancedendermen.EnhancedEndermen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.asavich.enhancedendermen.init.ItemInit.RED_ENDER_PEARL;

@Mod.EventBusSubscriber(modid = EnhancedEndermen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreativeTabInit {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnhancedEndermen.MOD_ID);
    public static final List<Supplier<? extends ItemLike>> MOD_TAB_ITEMS = new ArrayList<>();
    public static final RegistryObject<CreativeModeTab> MOD_TAB = TABS.register(
            "enhancedendermen",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.enhancedendermen"))
                    .icon(RED_ENDER_PEARL.get()::getDefaultInstance)
                    .displayItems((displayParams, output) -> MOD_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
                    .build()
    );

    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike) {
        MOD_TAB_ITEMS.add(itemLike);
        return itemLike;
    }

    //TODO SubscribeEvent
}

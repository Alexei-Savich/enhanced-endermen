package com.asavich.enhancedendermen;

import com.asavich.enhancedendermen.init.BlockInit;
import com.asavich.enhancedendermen.init.CreativeTabInit;
import com.asavich.enhancedendermen.init.EntityInit;
import com.asavich.enhancedendermen.init.ItemInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnhancedEndermen.MOD_ID)
public class EnhancedEndermen {
    public static final String MOD_ID = "enhancedendermen";

    public EnhancedEndermen() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(modEventBus);
        EntityInit.ENTITY_TYPES.register(modEventBus);
        CreativeTabInit.TABS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}

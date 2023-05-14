package com.asavich.enhancedendermen.client;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.client.render.TeleportationArrowRenderer;
import com.asavich.enhancedendermen.init.EntityInit;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EnhancedEndermen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityInit.TELEPORTATION_ARROW.get(), TeleportationArrowRenderer::new);
    }
}
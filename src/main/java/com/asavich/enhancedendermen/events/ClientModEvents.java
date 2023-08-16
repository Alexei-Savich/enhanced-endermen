package com.asavich.enhancedendermen.events;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.client.model.RedWandererModel;
import com.asavich.enhancedendermen.client.render.RedWandererRenderer;
import com.asavich.enhancedendermen.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnhancedEndermen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.RED_WANDERER.get(), RedWandererRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RedWandererModel.LAYER_LOCATION, RedWandererModel::createBodyLayer);
    }

}

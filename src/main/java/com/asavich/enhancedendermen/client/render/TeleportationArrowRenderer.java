package com.asavich.enhancedendermen.client.render;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.entities.TeleportationArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TeleportationArrowRenderer extends ArrowRenderer<TeleportationArrowEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(EnhancedEndermen.MOD_ID, "textures/entity/teleportation_arrow.png");

    public TeleportationArrowRenderer(EntityRendererProvider.Context manager) {
        super(manager);
    }

    public ResourceLocation getTextureLocation(TeleportationArrowEntity arrow) {
        return TEXTURE;
    }

}

package com.asavich.enhancedendermen.client.render;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.client.model.RedWandererModel;
import com.asavich.enhancedendermen.entities.RedWanderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;

public class RedWandererRenderer extends MobRenderer<RedWanderer, RedWandererModel<RedWanderer>>{
    private static final ResourceLocation RED_WANDERER_LOCATION = new ResourceLocation(EnhancedEndermen.MOD_ID, "textures/entity/red_wanderer.png");

    public RedWandererRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RedWandererModel<>(ctx.bakeLayer(RedWandererModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new RedWandererEyesLayer<>(this));
        this.addLayer(new RedWandererCarriedBlockLayer(this, ctx.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(RedWanderer p_114482_) {
        return RED_WANDERER_LOCATION;
    }

}

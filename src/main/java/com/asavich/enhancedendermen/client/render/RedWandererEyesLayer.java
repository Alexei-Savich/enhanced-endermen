package com.asavich.enhancedendermen.client.render;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.client.model.RedWandererModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class RedWandererEyesLayer <T extends LivingEntity> extends EyesLayer<T, RedWandererModel<T>> {
    private static final RenderType RED_WANDERER_EYES = RenderType.eyes(new ResourceLocation(EnhancedEndermen.MOD_ID, "textures/entity/red_wanderer_eyes.png"));

    public RedWandererEyesLayer(RenderLayerParent<T, RedWandererModel<T>> layerParent) {
        super(layerParent);
    }

    public @NotNull RenderType renderType() {
        return RED_WANDERER_EYES;
    }
}

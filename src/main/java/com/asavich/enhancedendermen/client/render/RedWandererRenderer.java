package com.asavich.enhancedendermen.client.render;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.client.model.RedWandererModel;
import com.asavich.enhancedendermen.entities.RedWanderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RedWandererRenderer extends MobRenderer<RedWanderer, RedWandererModel<RedWanderer>> {
    private static final ResourceLocation RED_WANDERER_LOCATION = new ResourceLocation(EnhancedEndermen.MOD_ID, "textures/entity/red_wanderer.png");
    private final RandomSource random = RandomSource.create();

    public RedWandererRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RedWandererModel<>(ctx.bakeLayer(RedWandererModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new RedWandererEyesLayer<>(this));
        this.addLayer(new RedWandererCarriedBlockLayer(this, ctx.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(RedWanderer p_114482_) {
        return RED_WANDERER_LOCATION;
    }

    public void render(RedWanderer p_114339_, float p_114340_, float p_114341_, PoseStack p_114342_, MultiBufferSource p_114343_, int p_114344_) {
        BlockState blockstate = p_114339_.getCarriedBlock();
        RedWandererModel<RedWanderer> redWandererModel = this.getModel();
        redWandererModel.carrying = blockstate != null;
        redWandererModel.creepy = p_114339_.isCreepy();
        super.render(p_114339_, p_114340_, p_114341_, p_114342_, p_114343_, p_114344_);
    }

    public Vec3 getRenderOffset(RedWanderer p_114336_, float p_114337_) {
        if (p_114336_.isCreepy()) {
            return new Vec3(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
        } else {
            return super.getRenderOffset(p_114336_, p_114337_);
        }
    }

}

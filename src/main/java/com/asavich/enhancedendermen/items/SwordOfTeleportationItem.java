package com.asavich.enhancedendermen.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SwordOfTeleportationItem extends SwordItem {
    public SwordOfTeleportationItem(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker){
        target.teleportTo(target.getX(), target.getY() + 10, target.getZ());
        return super.hurtEnemy(stack, target, attacker);
    }
}

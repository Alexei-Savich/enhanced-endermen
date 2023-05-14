package com.asavich.enhancedendermen.items;

import com.asavich.enhancedendermen.entities.TeleportationArrowEntity;
import com.asavich.enhancedendermen.init.EntityInit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TeleportationArrowItem extends ArrowItem {

    public TeleportationArrowItem(Properties props) {
        super(props);
    }

    @Override
    public AbstractArrow createArrow(Level world, ItemStack ammoStack, LivingEntity shooter) {
        return new TeleportationArrowEntity(EntityInit.TELEPORTATION_ARROW.get(), shooter, world);
    }

}

package com.asavich.enhancedendermen.entities;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

public class TeleportationArrowEntity extends AbstractArrow {

    private static final Logger LOGGER = LogUtils.getLogger();

    public TeleportationArrowEntity(EntityType<TeleportationArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public TeleportationArrowEntity(EntityType<TeleportationArrowEntity> entityType, double x, double y, double z, Level world) {
        super(entityType, x, y, z, world);
    }

    public TeleportationArrowEntity(EntityType<TeleportationArrowEntity> entityType, LivingEntity owner, Level world) {
        super(entityType, owner, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        LOGGER.info("Picked up a teleportation arrow");
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult entity) {
        LOGGER.info("Hit entity {}", entity.getEntity());
        if (this.getOwner() != null) {
            int origX = this.getOwner().getBlockX();
            int origY = this.getOwner().getBlockY();
            int origZ = this.getOwner().getBlockZ();
            this.getOwner().teleportTo(entity.getEntity().getX(), entity.getEntity().getY(), entity.getEntity().getZ());
            LOGGER.info("Teleporting entity to player position: {}, {}, {}", origX, origY, origZ);
            entity.getEntity().teleportTo(origX, origY, origZ);
        }
        super.onHitEntity(entity);
    }

    @Override
    protected void onHitBlock(BlockHitResult block) {
        super.onHitBlock(block);
        LOGGER.info("Hit block at: {}", block.getBlockPos());
        if (this.getOwner() != null) {
            this.getOwner().teleportTo(block.getBlockPos().getX(), block.getBlockPos().getY(), block.getBlockPos().getZ());
        }
    }

    @Override
    protected void tickDespawn() {
        if (this.inGroundTime > 0) {
            LOGGER.info("Removing arrow");
            this.discard();
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

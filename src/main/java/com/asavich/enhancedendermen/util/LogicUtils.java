package com.asavich.enhancedendermen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogicUtils {

    public static List<Block> getAllBlocksInSquareRadius(BlockPos pos, int radius, Level level){
        List<Block> res = new ArrayList<>();
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                for (int y = pos.getY() - radius; y < pos.getY() + radius; y++) {
                    res.add(level.getBlockState(new BlockPos(x, y, z)).getBlock());
                }
            }
        }
        return res;
    }

    public static double calculateDistanceBetweenEntities(LivingEntity e1, LivingEntity e2){
        return e1.distanceTo(e2);
    }

    public static boolean randomTp(LivingEntity target, RandomSource random){
        if (!target.level().isClientSide() && target.isAlive()) {
            double d0 = target.getX() + (random.nextDouble() - 0.5D) * 64.0D;
            double d1 = target.getY() + (double)(random.nextInt(64) - 32);
            double d2 = target.getZ() + (random.nextDouble() - 0.5D) * 64.0D;
            return target.randomTeleport(d0, d1, d2, true);
        } else {
            return false;
        }
    }

}

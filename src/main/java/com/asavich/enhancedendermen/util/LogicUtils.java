package com.asavich.enhancedendermen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

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

}

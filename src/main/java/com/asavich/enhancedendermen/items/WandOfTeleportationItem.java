package com.asavich.enhancedendermen.items;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import static com.asavich.enhancedendermen.util.LogicUtils.randomTp;

public class WandOfTeleportationItem extends SwordItem {

    private final int NUM_OF_TP_ATTEMPTS = 5;
    private final RandomSource random = RandomSource.create();

    public WandOfTeleportationItem(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean isTpSuccess = randomTp(target, random);
        for (int i = 0; i < NUM_OF_TP_ATTEMPTS; i++) {
            if (isTpSuccess) {
                break;
            }
            isTpSuccess = randomTp(target, random);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}

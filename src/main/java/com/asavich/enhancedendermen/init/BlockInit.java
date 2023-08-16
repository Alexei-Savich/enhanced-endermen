package com.asavich.enhancedendermen.init;

import com.asavich.enhancedendermen.EnhancedEndermen;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnhancedEndermen.MOD_ID);

    public static final RegistryObject<Block> REPELLER = BLOCKS.register("repeller",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .lightLevel((state) -> 11)
                    .strength(1f, 8f)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .pushReaction(PushReaction.NORMAL)
            ));
}

package com.asavich.enhancedendermen.events;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.entities.RedWanderer;
import com.asavich.enhancedendermen.init.EntityInit;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnhancedEndermen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.RED_WANDERER.get(), RedWanderer.createRedWandererAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacement(SpawnPlacementRegisterEvent event){
        event.register(
                EntityInit.RED_WANDERER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RedWanderer::canSpawn,
                SpawnPlacementRegisterEvent.Operation.OR
        );
    }

}

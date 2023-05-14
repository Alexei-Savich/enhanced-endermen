package com.asavich.enhancedendermen.init;

import com.asavich.enhancedendermen.EnhancedEndermen;
import com.asavich.enhancedendermen.entities.TeleportationArrowEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {

    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EnhancedEndermen.MOD_ID);

    public static final RegistryObject<EntityType<TeleportationArrowEntity>> TELEPORTATION_ARROW = ENTITY_TYPES.register("teleportation_arrow",
            () -> EntityType.Builder.of((EntityType.EntityFactory<TeleportationArrowEntity>) TeleportationArrowEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build("teleportation_arrow")
    );

}

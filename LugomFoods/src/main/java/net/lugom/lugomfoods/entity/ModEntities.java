package net.lugom.lugomfoods.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.entity.custom.TomatoThrowableEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<TomatoThrowableEntity> TOMATO_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(LugomFoods.MOD_ID, "tomato_throwable"),
            FabricEntityTypeBuilder.<TomatoThrowableEntity>create(SpawnGroup.MISC, TomatoThrowableEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());
}

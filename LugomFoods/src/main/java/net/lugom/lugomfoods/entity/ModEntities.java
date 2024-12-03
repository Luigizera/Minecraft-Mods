package net.lugom.lugomfoods.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.entity.client.TomatoDudeModel;
import net.lugom.lugomfoods.entity.client.TomatoDudeRenderer;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.lugom.lugomfoods.entity.custom.TomatoThrowableEntity;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<TomatoDudeEntity> TOMATO_DUDE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(LugomFoods.MOD_ID, "tomato_dude"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TomatoDudeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .build());

    public static final EntityType<TomatoThrowableEntity> TOMATO_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(LugomFoods.MOD_ID, "tomato_throwable"),
            FabricEntityTypeBuilder.<TomatoThrowableEntity>create(SpawnGroup.MISC, TomatoThrowableEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build());

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering entities");
        FabricDefaultAttributeRegistry.register(ModEntities.TOMATO_DUDE, TomatoDudeEntity.createAttributes());
    }

    public static void registerEntitiesRenderers() {
        EntityRendererRegistry.register(ModEntities.TOMATO_PROJECTILE, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TOMATO_DUDE, TomatoDudeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(TomatoDudeModel.TOMATO_DUDE_MODEL, TomatoDudeModel::getTexturedModelData);
    }
}

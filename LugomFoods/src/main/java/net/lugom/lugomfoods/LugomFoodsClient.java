package net.lugom.lugomfoods;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lugom.lugomfoods.block.ModBlocks;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.entity.client.TomatoDudeModel;
import net.lugom.lugomfoods.entity.client.TomatoDudeRenderer;
import net.lugom.lugomfoods.particles.ModParticles;
import net.lugom.lugomfoods.particles.custom.TomatoParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class LugomFoodsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TOMATO_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STRAWBERRY_CROP, RenderLayer.getCutout());
        EntityRendererRegistry.register(ModEntities.TOMATO_PROJECTILE, FlyingItemEntityRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.TOMATO_PARTICLE, TomatoParticle.Factory::new);
        EntityRendererRegistry.register(ModEntities.TOMATO_DUDE, TomatoDudeRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(TomatoDudeModel.TOMATO_DUDE_MODEL, TomatoDudeModel::getTexturedModelData);

    }
}

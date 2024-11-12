package net.lugom.lugomfoods.entity.client;

import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TomatoDudeRenderer extends MobEntityRenderer<TomatoDudeEntity, TomatoDudeModel<TomatoDudeEntity>> {
    private static int num = 0;
    private static int cooldown = 0;
    private static final Identifier TEXTURE = new Identifier(LugomFoods.MOD_ID, "textures/entity/tomato_dude.png");
    private static final Identifier TEXTURE_BLINK = new Identifier(LugomFoods.MOD_ID, "textures/entity/tomato_dude_blink.png");
    private static final Identifier TEXTURE_TAMED = new Identifier(LugomFoods.MOD_ID, "textures/entity/tomato_dude_tamed.png");
    private static final Identifier TEXTURE_TAMED_BLINK = new Identifier(LugomFoods.MOD_ID, "textures/entity/tomato_dude_tamed_blink.png");

    public TomatoDudeRenderer(EntityRendererFactory.Context context) {
        super(context, new TomatoDudeModel<>(context.getPart(TomatoDudeModel.TOMATO_DUDE_MODEL)), 0.6f);
    }

    @Override
    public Identifier getTexture(TomatoDudeEntity entity) {
        num++;
        if(num >= 3600) { //60*60
            num = 0;
            cooldown = 240; //4*60
            if(entity.isTamed()) {
                return TEXTURE_TAMED_BLINK;
            }
            return TEXTURE_BLINK;
        }

        if(cooldown > 0) {
            cooldown--;
            num = 0;
            if(entity.isTamed()) {
                return TEXTURE_TAMED_BLINK;
            }
            return TEXTURE_BLINK;
        }
        if(entity.isTamed()) {
            return TEXTURE_TAMED;
        }
        return TEXTURE;
    }

    @Override
    public void render(TomatoDudeEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        else {
            matrixStack.scale(1f, 1f, 1f);
        }


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}

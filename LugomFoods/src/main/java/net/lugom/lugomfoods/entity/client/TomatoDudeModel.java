// Made with Blockbench 4.7.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.lugom.lugomfoods.entity.client;

import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TomatoDudeModel<T extends TomatoDudeEntity> extends SinglePartEntityModel<T> {
	public static final EntityModelLayer TOMATO_DUDE_MODEL =
			new EntityModelLayer(new Identifier(LugomFoods.MOD_ID, "tomato_dude"), "main");
	private final ModelPart tomato;
	private final ModelPart head;
	private final ModelPart lettuce;
	private final ModelPart legs;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public TomatoDudeModel(ModelPart root) {
		this.tomato = root.getChild("tomato");
		this.head = tomato.getChild("head");
		this.lettuce = head.getChild("lettuce");
		this.legs = tomato.getChild("legs");
		this.left_leg = legs.getChild("left_leg");
		this.right_leg = legs.getChild("right_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData tomato = modelPartData.addChild("tomato", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 20.0F, 0.0F));

		ModelPartData head = tomato.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -4.5F, -3.0F, 6.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.5F, 0.0F));

		ModelPartData lettuce = head.addChild("lettuce", ModelPartBuilder.create().uv(8, 15).cuboid(-1.0F, 0.6F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -5.6F, 0.0F));

		ModelPartData front_r1 = lettuce.addChild("front_r1", ModelPartBuilder.create().uv(0, 15).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4F, 0.0F, 0.3491F, 0.0F, 0.0F));

		ModelPartData back_r1 = lettuce.addChild("back_r1", ModelPartBuilder.create().uv(14, 13).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4F, 0.0F, -0.3491F, 0.0F, 0.0F));

		ModelPartData left_r1 = lettuce.addChild("left_r1", ModelPartBuilder.create().uv(0, 11).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4F, 0.0F, 0.0F, 0.0F, 0.3491F));

		ModelPartData right_r1 = lettuce.addChild("right_r1", ModelPartBuilder.create().uv(8, 11).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4F, 0.0F, 0.0F, 0.0F, -0.3491F));

		ModelPartData legs = tomato.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

		ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(18, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 0.0F, 0.0F));

		ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(14, 17).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		tomato.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return tomato;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(headYaw, headPitch);

		this.animateMovement(TomatoDudeAnimations.TOMATO_DUDE_WALK, limbAngle, limbDistance, 2f, 2.5f);
		this.updateAnimation(entity.idleAnimationState, TomatoDudeAnimations.TOMATO_DUDE_IDLE, animationProgress, 1f);
		this.updateAnimation(entity.sittingAnimationState, TomatoDudeAnimations.TOMATO_DUDE_SIT, animationProgress, 1f);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
		headPitch = MathHelper.clamp(headPitch, -25.0F,45.0F);
		this.head.yaw = headYaw * 0.017F;
		this.head.pitch = headPitch * 0.017F;
	}
}
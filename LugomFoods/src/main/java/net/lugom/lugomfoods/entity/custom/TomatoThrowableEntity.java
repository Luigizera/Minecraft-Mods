package net.lugom.lugomfoods.entity.custom;

import com.google.common.base.MoreObjects;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.item.ModItems;
import net.lugom.lugomfoods.particles.ModParticles;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class TomatoThrowableEntity extends ThrownItemEntity {
    public TomatoThrowableEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public TomatoThrowableEntity(LivingEntity livingEntity, World world) {
        super(ModEntities.TOMATO_PROJECTILE, livingEntity, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.TOMATO_THROWABLE;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getItem();
        return (ParticleEffect)(itemStack.isEmpty() ? ModParticles.TOMATO_PARTICLE : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();
            for (int i = 0; i < 50; i++) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 2, 2, 2);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        float damage = owner instanceof LivingEntity ? 0.20f : 0.0f;
        LivingEntity ownerLivingEntity = owner instanceof LivingEntity ? (LivingEntity)owner : null;
        boolean bl = entity.damage(this.getDamageSources().thrown(this, ownerLivingEntity), damage);
        if (bl) {
            if (entity instanceof LivingEntity livingEntity2) {
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10*20), MoreObjects.firstNonNull(owner, this));
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10*20), MoreObjects.firstNonNull(owner, this));
            }
        }
    }
    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
            if(this.getOwner() instanceof PlayerEntity) {
                this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(ModItems.TOMATO_SEEDS, random.nextBetween(0, 4))));
            }
        }
    }
}

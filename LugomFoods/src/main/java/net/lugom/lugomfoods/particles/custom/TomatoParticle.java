package net.lugom.lugomfoods.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class TomatoParticle extends SpriteBillboardParticle {

    protected TomatoParticle(ClientWorld level, double x, double y, double z,
                              SpriteProvider spriteSet, double velx, double vely, double velz) {
        super(level, x, y, z, velx, vely, velz);

        this.velocityX += (velx * random.nextBetween(-1, 1)) * 0.1;
        this.velocityY += (vely * random.nextBetween(-1, 1)) * 0.1;
        this.velocityZ += (velz * random.nextBetween(-1, 1)) * 0.1;
        this.gravityStrength = 1.0F;
        this.scale *= 0.5F;
        this.maxAge = 30;
        this.setSpriteForAge(spriteSet);
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double velx, double vely, double velz) {
            return new TomatoParticle(level, x, y, z, this.sprites, velx, vely, velz);
        }
    }
}

package net.lugom.lugomfoods.particles;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.lugom.lugomfoods.LugomFoods;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType TOMATO_PARTICLE = register("tomato_particle", FabricParticleTypes.simple());

    private static <T extends DefaultParticleType> T register(String name, T particle) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(LugomFoods.MOD_ID, name), particle);
    }

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering particles");
    }
}

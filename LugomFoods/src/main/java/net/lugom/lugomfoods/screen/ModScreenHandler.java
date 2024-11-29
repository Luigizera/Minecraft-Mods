package net.lugom.lugomfoods.screen;

import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.screen.custom.TomatoScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandler {
    public static final ScreenHandlerType<TomatoScreenHandler> TOMATO_DUDE_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(LugomFoods.MOD_ID, "tomato_dude_screen_handler"),
            new ScreenHandlerType<>(TomatoScreenHandler::new, FeatureSet.empty()));

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering Screen Handlers");
    }
}

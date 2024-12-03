package net.lugom.lugomfoods.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.screen.custom.TomatoScreen;
import net.lugom.lugomfoods.screen.custom.TomatoScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandler {
    public static final ScreenHandlerType<TomatoScreenHandler> TOMATO_DUDE_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(LugomFoods.MOD_ID, "tomato_dude_screen_handler"),
            new ExtendedScreenHandlerType<>(TomatoScreenHandler::new));

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering Screen Handlers");
    }

    public static void registerScreenRenderers(){
        HandledScreens.register(ModScreenHandler.TOMATO_DUDE_SCREEN_HANDLER, TomatoScreen::new);
    }
}

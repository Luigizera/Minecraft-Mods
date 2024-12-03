package net.lugom.lugomfoods;

import net.fabricmc.api.ModInitializer;
import net.lugom.lugomfoods.block.ModBlocks;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.item.ModItems;
import net.lugom.lugomfoods.item.ModItemsGroup;
import net.lugom.lugomfoods.particles.ModParticles;
import net.lugom.lugomfoods.screen.ModScreenHandler;
import net.lugom.lugomfoods.village.ModVillagerTrades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LugomFoods implements ModInitializer {
	public static final String MOD_ID = "lugomfoods";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModBlocks.initialize();
		ModItems.initialize();
		ModItemsGroup.initialize();
		ModParticles.initialize();
		ModVillagerTrades.initialize();
		ModEntities.initialize();
		ModScreenHandler.initialize();
	}
}
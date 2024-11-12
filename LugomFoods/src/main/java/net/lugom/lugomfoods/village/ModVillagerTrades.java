package net.lugom.lugomfoods.village;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.lugom.lugomfoods.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class ModVillagerTrades {;
    public static void initialize() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            //TOMATO TRADES
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.INGREDIENTS_SIMPLE[random.nextInt(ModItems.INGREDIENTS_SIMPLE.length-1)], 1),
                    new ItemStack(Items.WHEAT, random.nextBetween(4, 8)), 5, 2, 0.05f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, random.nextBetween(2, 6)),
                    new ItemStack(ModItems.INGREDIENTS_SIMPLE[random.nextInt(ModItems.INGREDIENTS_SIMPLE.length-1)], random.nextBetween(1, 4)), 5, 5, 0.05f)
            );

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.INGREDIENTS_SIMPLE[random.nextInt(ModItems.INGREDIENTS_SIMPLE.length-1)], random.nextBetween(4, 10)),
                    new ItemStack(Items.EMERALD, random.nextBetween(1, 3)), 5, 5, 0.05f)
            );

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.SEEDS[random.nextInt(ModItems.SEEDS.length-1)], random.nextBetween(4, 8)),
                    new ItemStack(Items.EMERALD, random.nextBetween(1, 4)), 5, 2, 0.05f)
            );

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, random.nextBetween(2, 6)),
                    new ItemStack(ModItems.SEEDS[random.nextInt(ModItems.SEEDS.length-1)], random.nextBetween(1, 4)), 5, 2, 0.05f)
            );

            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.INGREDIENTS_GREEN[random.nextInt(ModItems.INGREDIENTS_GREEN.length-1)], 1),
                    new ItemStack(Items.WHEAT, random.nextBetween(1, 3)), 5, 2, 0.05f)
            );
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 2, factories -> {
            //TOMATO TRADES
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, random.nextBetween(7, 14)),
                    new ItemStack(ModItems.INGREDIENTS_GOLDEN[random.nextInt(ModItems.INGREDIENTS_GOLDEN.length-1)], random.nextBetween(1, 4)), 5, 10, 0.08f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.INGREDIENTS_GOLDEN[random.nextInt(ModItems.INGREDIENTS_GOLDEN.length-1)], random.nextBetween(24, 48)),
                    new ItemStack(ModItems.INGREDIENTS_SIMPLE[random.nextInt(ModItems.INGREDIENTS_SIMPLE.length-1)], random.nextBetween(32, 64)),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, random.nextBetween(1, 4)), 2, 10, 0.08f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.INGREDIENTS_GOLDEN[random.nextInt(ModItems.INGREDIENTS_GOLDEN.length-1)], random.nextBetween(32, 64)),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, random.nextBetween(1, 4)), 2, 10, 0.08f)
            );
        });
    }
}

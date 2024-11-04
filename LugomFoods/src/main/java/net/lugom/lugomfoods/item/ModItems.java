package net.lugom.lugomfoods.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.block.ModBlocks;
import net.lugom.lugomfoods.item.custom.TomatoThrowableItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item TOMATO = register("tomato", new Item(new FabricItemSettings().food(ModFoodComponents.INGREDIENT_TYPE)));
    public static final Item TOMATO_GOLDEN = register("tomato_golden", new Item(new FabricItemSettings().rarity(Rarity.RARE).food(ModFoodComponents.INGREDIENT_GOLDEN_TYPE)));
    public static final Item TOMATO_THROWABLE = register("tomato_throwable", new TomatoThrowableItem(new FabricItemSettings(), true));
    public static final Item TOMATO_SEEDS = register("tomato_seeds", new AliasedBlockItem(ModBlocks.TOMATO_CROP, new FabricItemSettings()));

    private static void addItemsToIngredientsGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO);
        entries.add(TOMATO_GOLDEN);
    }
    private static void addItemsToCombatGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO_THROWABLE);
    }

    private static void addItemsToNaturalGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO_SEEDS);
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(LugomFoods.MOD_ID, name), item);
    }

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering items");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientsGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModItems::addItemsToNaturalGroup);
    }

    private static class ModFoodComponents {
        protected static final FoodComponent INGREDIENT_TYPE = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.6F)
                .snack()
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5*20),  0.05f)
                .build();
        protected static final FoodComponent INGREDIENT_GOLDEN_TYPE = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(1.2F)
                .snack()
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 10*20),  0.05f)
                .build();
    }
}

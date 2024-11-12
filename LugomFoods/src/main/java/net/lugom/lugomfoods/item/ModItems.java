package net.lugom.lugomfoods.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.block.ModBlocks;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.item.custom.TomatoThrowableItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item TOMATO = register("tomato", new Item(new FabricItemSettings().food(ModFoodComponents.TOMATO)));
    public static final Item TOMATO_GREEN = register("tomato_green", new Item(new FabricItemSettings().food(ModFoodComponents.TOMATO_GREEN)));
    public static final Item TOMATO_GOLDEN = register("tomato_golden", new Item(new FabricItemSettings().rarity(Rarity.RARE).food(ModFoodComponents.TOMATO_GOLDEN)));
    public static final Item TOMATO_THROWABLE = register("tomato_throwable", new TomatoThrowableItem(new FabricItemSettings(), true));
    public static final Item TOMATO_SEEDS = register("tomato_seeds", new AliasedBlockItem(ModBlocks.TOMATO_CROP, new FabricItemSettings()));
    public static final Item STRAWBERRY = register("strawberry", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY)));
    public static final Item STRAWBERRY_GREEN = register("strawberry_green", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY_GREEN)));
    public static final Item STRAWBERRY_GOLDEN = register("strawberry_golden", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY_GOLDEN)));
    public static final Item STRAWBERRY_SEEDS = register("strawberry_seeds", new AliasedBlockItem(ModBlocks.STRAWBERRY_CROP, new FabricItemSettings()));
    public static final Item TOMATO_DUDE_SPAWN_EGG = register("tomato_dude_spawn_egg", new SpawnEggItem(ModEntities.TOMATO_DUDE, 0x000000, 0xFFFFFF,new FabricItemSettings()));

    public static final Item[] INGREDIENTS_SIMPLE = new Item[]{TOMATO, STRAWBERRY};
    public static final Item[] INGREDIENTS_GREEN = new Item[]{TOMATO_GREEN, STRAWBERRY_GREEN};
    public static final Item[] INGREDIENTS_GOLDEN = new Item[]{TOMATO_GOLDEN, STRAWBERRY_GOLDEN};
    public static final Item[] SEEDS = new Item[]{TOMATO_SEEDS, STRAWBERRY_SEEDS};


    private static void addItemsToIngredientsGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO);
        entries.add(TOMATO_GREEN);
        entries.add(TOMATO_GOLDEN);
        entries.add(STRAWBERRY);
        entries.add(STRAWBERRY_GREEN);
        entries.add(STRAWBERRY_GOLDEN);
    }
    private static void addItemsToCombatGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO_THROWABLE);
    }

    private static void addItemsToNaturalGroup(FabricItemGroupEntries entries) {
        entries.add(TOMATO_SEEDS);
        entries.add(STRAWBERRY_SEEDS);
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
        protected static final FoodComponent TOMATO = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.6f)
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 5*20),  0.05f)
                .build();
        protected static final FoodComponent TOMATO_GOLDEN = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(1.2f)
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 10*20),  0.05f)
                .build();
        protected static final FoodComponent TOMATO_GREEN = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.3f)
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 5*20), 0.6f)
                .build();
        protected static final FoodComponent STRAWBERRY = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.6f)
                .snack()
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10*20),  0.1f)
                .build();
        protected static final FoodComponent STRAWBERRY_GOLDEN = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(1.2f)
                .snack()
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10*20, 1),  0.1f)
                .build();
        protected static final FoodComponent STRAWBERRY_GREEN = new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.3f)
                .snack()
                .alwaysEdible()
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5*20), 0.6f)
                .build();
    }
}

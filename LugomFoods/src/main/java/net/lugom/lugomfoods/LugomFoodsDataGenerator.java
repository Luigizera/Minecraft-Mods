package net.lugom.lugomfoods;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.lugom.lugomfoods.block.ModBlocks;
import net.lugom.lugomfoods.block.custom.TomatoCropBlock;
import net.lugom.lugomfoods.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LugomFoodsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		//pack.addProvider(ModBlockTagProvider::new);
		//pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(AdvancementsProvider::new);
	}

	private static class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
		public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

		}
	}

	private static class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
		public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

		}
	}

	private static class ModLootTableProvider extends FabricBlockLootTableProvider {
		public ModLootTableProvider(FabricDataOutput dataOutput) {
			super(dataOutput);
		}

		@Override
		public void generate() {
			addDrop(ModBlocks.TOMATO_CROP, cropDrops(ModBlocks.TOMATO_CROP, ModItems.TOMATO, ModItems.TOMATO_SEEDS,
					generateCropBuilder(ModBlocks.TOMATO_CROP, TomatoCropBlock.AGE, TomatoCropBlock.MAX_AGE)));
		}

		public static BlockStatePropertyLootCondition.Builder generateCropBuilder(Block block, IntProperty blockAge, int blockMax_Age) {
			return BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(blockAge, blockMax_Age));
		}
	}

	private static class ModModelProvider extends FabricModelProvider {
		public ModModelProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			blockStateModelGenerator.registerTintableCrossBlockStateWithStages(ModBlocks.TOMATO_CROP, BlockStateModelGenerator.TintType.NOT_TINTED, TomatoCropBlock.AGE,
					0, 1, 2, 3, 4, 5, 6);
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			itemModelGenerator.register(ModItems.TOMATO, Models.GENERATED);
			itemModelGenerator.register(ModItems.TOMATO_GREEN, Models.GENERATED);
			itemModelGenerator.register(ModItems.TOMATO_GOLDEN, Models.GENERATED);
			itemModelGenerator.register(ModItems.TOMATO_THROWABLE, Models.GENERATED);
		}
	}

	private static class ModRecipeProvider extends FabricRecipeProvider {
		public ModRecipeProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generate(Consumer<RecipeJsonProvider> consumer) {
			//TOMATO RECIPES
			//2x2 TOMATO -> 1 THROWABLE TOMATO
			//1 THROWABLE TOMATO -> 4 TOMATO
			//1 TOMATO GOLDEN -> 4 TOMATO
			//1 TOMATO -> 4 TOMATO SEEDS
			offerShapelessRecipe(consumer, ModItems.TOMATO, ModItems.TOMATO_THROWABLE, null, 4);
			offerShapelessRecipe(consumer, ModItems.TOMATO_SEEDS, ModItems.TOMATO, null, 4);
			offerShapelessRecipe(consumer, ModItems.TOMATO, ModItems.TOMATO_GOLDEN, null, 4);
			offer2x2CompactingRecipe(consumer, RecipeCategory.COMBAT, ModItems.TOMATO_THROWABLE, ModItems.TOMATO);
		}
	}

	static class AdvancementsProvider extends FabricAdvancementProvider {
		protected AdvancementsProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			Advancement root = Advancement.Builder.create()
					.display(
							ModItems.TOMATO, // The display icon
							Text.translatable("advancement.title." + LugomFoods.MOD_ID + ".plant_seed"), // The title
							Text.translatable("advancement.description." + LugomFoods.MOD_ID + ".plant_seed"), // The description
							new Identifier("textures/gui/advancements/backgrounds/husbandry.png"), // Background image used
							AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
							true, // Show toast top right
							true, // Announce to chat
							false // Hidden in the advancement tab
					)
					.criterion("got_tomato", ItemCriterion.Conditions.createPlacedBlock(ModBlocks.TOMATO_CROP))
					.build(consumer, LugomFoods.MOD_ID + "/root");

			Advancement got_tomato_golden = Advancement.Builder.create().parent(root)
					.display(
							ModItems.TOMATO_GOLDEN,
							Text.translatable("advancement.title." + LugomFoods.MOD_ID + ".got_golden_ingredient"),
							Text.translatable("advancement.description." + LugomFoods.MOD_ID + ".got_golden_ingredient"),
							null,
							AdvancementFrame.TASK,
							true,
							true,
							false // Hidden in the advancement tab
					)
					.criterion("got_tomato_golden", InventoryChangedCriterion.Conditions.items(ModItems.TOMATO_GOLDEN))
					.build(consumer, LugomFoods.MOD_ID + "/got_golden_ingredient");

		}
	}
}

package net.lugom.lugomfoods.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.block.custom.StrawberryCropBlock;
import net.lugom.lugomfoods.block.custom.TomatoCropBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block TOMATO_CROP = Registry.register(Registries.BLOCK,
                new Identifier(LugomFoods.MOD_ID, "tomato_crop"),
                new TomatoCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)
                        .sounds(BlockSoundGroup.STEM)));
    public static final Block STRAWBERRY_CROP = Registry.register(Registries.BLOCK,
            new Identifier(LugomFoods.MOD_ID, "strawberry_crop"),
            new StrawberryCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)
                    .sounds(BlockSoundGroup.SWEET_BERRY_BUSH)));

    private static <T extends Block> T register(String name, T block) {
        Registry.register(Registries.ITEM, Identifier.of(LugomFoods.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, Identifier.of(LugomFoods.MOD_ID, name), block);
    }

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID + ": Registering blocks");
    }
}

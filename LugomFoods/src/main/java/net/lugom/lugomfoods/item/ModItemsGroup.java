package net.lugom.lugomfoods.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.lugom.lugomfoods.LugomFoods;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemsGroup {
    public static final ItemGroup MOD_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(LugomFoods.MOD_ID, "tomato"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.lugommod.tomato"))
                    .icon(() -> new ItemStack(ModItems.TOMATO)).entries((displayContext, entries) -> {
                        entries.add(ModItems.TOMATO);
                        entries.add(ModItems.TOMATO_GOLDEN);
                        entries.add(ModItems.TOMATO_THROWABLE);
                        entries.add(ModItems.TOMATO_SEEDS);
                    }).build());

    public static void initialize() {
        LugomFoods.LOGGER.info(LugomFoods.MOD_ID +": Registering item group");
    }
}

package net.lugom.lugomfoods.screen.custom;

import net.lugom.lugomfoods.screen.ModScreenHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class TomatoScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    // This constructor gets called on the client when the server wants it to open the screenHandler,
    // The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    // sync this empty inventory with the inventory on the server.
    public TomatoScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(5));
    }

    // This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    // and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public TomatoScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandler.TOMATO_DUDE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 5);
        this.inventory = inventory;
        // some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);

        int w;
        int h;
        // Our inventory
        for (w = 0; w < 5; ++w) {
            this.addSlot(new Slot(inventory, w, 44 + w * 18, 35));
        }
        // The player inventory
        for (w = 0; w < 3; ++w) {
            for (h = 0; h < 9; ++h) {
                this.addSlot(new Slot(playerInventory, h + w * 9 + 9, 8 + h * 18, 84 + w * 18));
            }
        }
        // The player Hotbar
        for (w = 0; w < 9; ++w) {
            this.addSlot(new Slot(playerInventory, w, 8 + w * 18, 142));
        }

    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}



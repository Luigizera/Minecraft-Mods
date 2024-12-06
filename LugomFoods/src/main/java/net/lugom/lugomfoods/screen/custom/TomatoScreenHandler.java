package net.lugom.lugomfoods.screen.custom;

import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.lugom.lugomfoods.screen.ModScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Objects;

public class TomatoScreenHandler extends ScreenHandler {
    private Inventory inventory;
    private TomatoDudeEntity entity;
    private int entityId;

    // This constructor gets called on the client when the server wants it to open the screenHandler,
    // The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    // sync this empty inventory with the inventory on the server.
    public TomatoScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(6), (TomatoDudeEntity) playerInventory.player.getWorld().getEntityById(buf.readInt()));
        this.entityId = buf.readInt();
        this.entity = (TomatoDudeEntity) playerInventory.player.getWorld().getEntityById(entityId);

    }

    // This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    // and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public TomatoScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, TomatoDudeEntity entity) {
        super(ModScreenHandler.TOMATO_DUDE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 6);
        this.inventory = inventory;
        this.entity = entity;
        this.inventory.onOpen(playerInventory.player);

        int w;
        int h;
        if(entity.hasChest()) {
            for (w = 0; w < 2; ++w) {
                for (h = 0; h < 3; ++h) {
                    this.addSlot(new Slot(inventory, h + w * entity.getInventoryColumns(), 98 + h * 18, 29 + w * 18));
                }
            }
        }

        addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        int w;
        int h;
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
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.markDirty();

        this.inventory.onClose(player);
    }

    public TomatoDudeEntity getEntity(){
        return entity;
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
            }
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
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



package net.lugom.lugomfoods.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lugom.lugomfoods.LugomFoods;
import net.lugom.lugomfoods.entity.ModEntities;
import net.lugom.lugomfoods.entity.custom.TomatoDudeEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TomatoScreen extends HandledScreen<TomatoScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(LugomFoods.MOD_ID, "textures/gui/container/tomato_dude_inventory.png");
    private final TomatoDudeEntity entity;

    public TomatoScreen(TomatoScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, text);
        this.entity = handler.getEntity();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (this.entity.hasChest()) {
            context.drawTexture(TEXTURE, x + 97, y + 28, 0, this.backgroundHeight, this.entity.getInventoryColumns() * 18, 36);
        }
        InventoryScreen.drawEntity(context, x + 50, y + 50, 30, (float)(x + 51) - mouseX, (float)(y + 75 - 50) - mouseY, this.entity);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}



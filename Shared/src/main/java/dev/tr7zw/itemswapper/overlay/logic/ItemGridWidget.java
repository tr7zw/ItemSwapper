package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;

public abstract class ItemGridWidget implements GuiWidget {
    
    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    protected final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    protected final List<GuiSlot> slots = new ArrayList<>();
    protected WidgetArea widgetArea = new WidgetArea(0, 0, 128, 128, null, 0, 0);

    protected ItemGridWidget(int x, int y) {
        this.widgetArea.setX(x);
        this.widgetArea.setY(y);
    }

    @Override
    public void render(GuiComponent parent, PoseStack poseStack, int originX, int originY, boolean overwrideAvailable) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (widgetArea.getBackgroundTexture() != null) {
            RenderSystem.setShaderTexture(0, widgetArea.getBackgroundTexture());
            GuiComponent.blit(poseStack, originX - (widgetArea.getBackgroundSizeX() / 2), originY - (widgetArea.getBackgroundSizeY() / 2), 0, 0,
                    widgetArea.getBackgroundSizeX(),
                    widgetArea.getBackgroundSizeY(), widgetArea.getBackgroundTextureSizeX(), widgetArea.getBackgroundTextureSizeY());
        }
        RenderSystem.setShaderTexture(0, WidgetUtil.WIDGETS_LOCATION);
        List<Runnable> itemRenderList = new ArrayList<>();
        List<Runnable> lateRenderList = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            renderSelection(parent, poseStack, i, originX + slots.get(i).x(), originY + slots.get(i).y(),
                    itemRenderList,
                    lateRenderList, overwrideAvailable);
        }
        itemRenderList.forEach(Runnable::run);
        float blit = this.itemRenderer.blitOffset;
        this.itemRenderer.blitOffset += 300;
        lateRenderList.forEach(Runnable::run);
        this.itemRenderer.blitOffset = blit;
    }

    protected abstract void renderSelection(GuiComponent parent, PoseStack poseStack, int listId, int x, int y,
            List<Runnable> itemRenderList,
            List<Runnable> lateRenderList,
            boolean overwrideAvailable);
    
    @Override
    public List<GuiSlot> getSlots() {
        return slots;
    }

    @Override
    public WidgetArea getWidgetArea() {
        return widgetArea;
    }

    @Override
    public int titleYOffset() {
        return widgetArea.getBackgroundSizeY();
    }

}
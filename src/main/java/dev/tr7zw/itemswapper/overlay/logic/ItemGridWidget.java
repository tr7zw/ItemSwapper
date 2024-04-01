package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

    public void render(Screen parent, GuiGraphics graphics, int originX, int originY, boolean overwrideAvailable) {
        originX += getWidgetArea().getX();
        originY += getWidgetArea().getY();
        WidgetUtil.renderBackground(getWidgetArea(), graphics, originX, originY);
        List<Runnable> itemRenderList = new ArrayList<>();
        List<Runnable> lateRenderList = new ArrayList<>();
        for (int i = 0; i < getSlots().size(); i++) {
            renderSelection(parent, graphics, i, originX + getSlots().get(i).x(), originY + getSlots().get(i).y(),
                    itemRenderList, lateRenderList, overwrideAvailable);
        }
        RenderSystem.enableBlend();
        itemRenderList.forEach(Runnable::run);
        RenderSystem.enableBlend();
        lateRenderList.forEach(Runnable::run);
    }

    private void renderSelection(Screen parent, GuiGraphics graphics, int listId, int x, int y,
            List<Runnable> itemRenderList, List<Runnable> lateRenderList, boolean overwrideAvailable) {
        if (getWidgetArea().getBackgroundTexture() == null) {
            // fallback in case of no background texture
            // spotless:off 
          //#if MC >= 12002
            graphics.blitSprite(WidgetUtil.HOTBAR_OFFHAND_LEFT_SPRITE, x, y, 29, 24);
          //#else
          //$$ graphics.blit(WidgetUtil.WIDGETS_LOCATION, x, y, 24, 22, 29, 24);   
          //#endif
          //spotless:on
        }
        GuiSlot guiSlot = getSlots().get(listId);
        if (guiSlot.selected().get()) {
            itemRenderList = lateRenderList;
            graphics.blit(WidgetUtil.SELECTION_LOCATION, x - 1, y, 200, 0, 0, 24, 24, 24, 24);
        }
        renderSlot(graphics, x, y, itemRenderList, guiSlot, overwrideAvailable);
    }

    protected abstract void renderSlot(GuiGraphics graphics, int x, int y, List<Runnable> itemRenderList,
            GuiSlot guiSlot, boolean overwrideAvailable);

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
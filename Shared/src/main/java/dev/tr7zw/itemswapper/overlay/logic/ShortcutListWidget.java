package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ShortcutListWidget implements GuiWidget {

    private static final Minecraft minecraft = Minecraft.getInstance();
    
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final List<GuiSlot> slots = new ArrayList<>();
    private final List<Shortcut> list;
    private WidgetArea widgetArea = new WidgetArea(0, 0, 128, 128, null, 0, 0);

    public ShortcutListWidget(List<Shortcut> list, int x, int y) {
        this.list = list;
        this.widgetArea.setX(x);
        this.widgetArea.setY(y);
        WidgetUtil.setupSlots(widgetArea, slots, 1, list.size(), false, null);
    }
    
    @Override
    public List<GuiSlot> getSlots() {
        return slots;
    }

    @Override
    public WidgetArea getWidgetArea() {
        return widgetArea;
    }

    @Override
    public void render(GuiComponent parent, PoseStack poseStack, int originX, int originY, boolean overwrideAvailable) {
        originX += widgetArea.getX();
        originY += widgetArea.getY();
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
    
    protected void renderSelection(GuiComponent parent, PoseStack poseStack, int listId, int x, int y,
            List<Runnable> itemRenderList,
            List<Runnable> lateRenderList,
            boolean overwrideAvailable) {
        if (widgetArea.getBackgroundTexture() == null) {
            parent.blit(poseStack, x, y, 24, 22, 29, 24);
        }
        GuiSlot guiSlot = slots.get(listId);
        if (guiSlot.selected().get()) {
            itemRenderList = lateRenderList;
            lateRenderList.add(() -> {
                float blit = parent.getBlitOffset();
                parent.setBlitOffset((int) this.itemRenderer.blitOffset);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, WidgetUtil.SELECTION_LOCATION);
                GuiComponent.blit(poseStack, x - 1, y, parent.getBlitOffset(), 0, 0, 24, 24, 24, 24);
                parent.setBlitOffset((int) blit);
            });
        }
        ItemEntry item = list.get(guiSlot.id()).getIcon();
        itemRenderList.add(
                () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player,
                        item.getItem().getDefaultInstance(), 1,
                        false, 1));
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, boolean overwrideAvailable) {
        ItemEntry slot = list.get(selected.id()).getIcon();
        RenderHelper.renderSelectedItemName(RenderHelper.getName(slot),
                slot.getItem().getDefaultInstance(), false, yOffset);
    }

    @Override
    public void onClick(SwitchItemOverlay overlay, GuiSlot slot) {
        Shortcut shortcut = list.get(slot.id());
        if(shortcut.acceptClick()) {
            shortcut.invoke();
        }
    }

    @Override
    public void onClose(SwitchItemOverlay overlay, GuiSlot slot) {
        Shortcut shortcut = list.get(slot.id());
        if(shortcut.acceptClose()) {
            shortcut.invoke();
        }
    }

}

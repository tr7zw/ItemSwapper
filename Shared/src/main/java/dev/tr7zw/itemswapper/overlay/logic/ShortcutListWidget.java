package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
import dev.tr7zw.itemswapper.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class ShortcutListWidget implements GuiWidget {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation SELECTION_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/selection.png");
    
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final List<GuiSlot> slots = new ArrayList<>();
    private final List<Shortcut> list;
    private final int x;
    private final int y;
    private int backgroundSizeX = 0;
    private int backgroundSizeY = 0;
    private int backgroundTextureSizeX = 128;
    private int backgroundTextureSizeY = 128;
    protected ResourceLocation backgroundTexture = null;
    private int mouseBoundsX = 0;
    private int mouseBoundsY = 0;
    
    public ShortcutListWidget(List<Shortcut> list, int x, int y) {
        this.list = list;
        this.x = x;
        this.y = y;
        setupSlots(1, list.size(), false, null);
    }
    
    private void setupSlots(int width, int height, boolean skipCorners, ResourceLocation texture) {
        backgroundTexture = texture;
        backgroundSizeX = width * XTOverlay.tinySlotSize + 6;
        backgroundSizeY = height * XTOverlay.tinySlotSize + 6;
        int sz = texture == null ? XTOverlay.slotSize : XTOverlay.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        mouseBoundsX = width * lz;
        mouseBoundsY = height * lz;
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = skipCorners
                        && ((x == 0 && y == 0) || (x == 0 && y == height - 1) || (x == width - 1 && y == height - 1)
                                || (x == width - 1 && y == 0));
                if (!skip) {
                    slots.add(new GuiSlot(originX + x * sz, originY + y * sz, slotId,
                            XTOverlay.tinySlotSize));
                    slotId++;
                }
            }
        }
    }
    
    @Override
    public List<GuiSlot> getSlots() {
        return slots;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getMouseBoundsX() {
        return mouseBoundsX;
    }

    @Override
    public int getMouseBoundsY() {
        return mouseBoundsY;
    }

    @Override
    public void render(GuiComponent parent, PoseStack poseStack, int originX, int originY, boolean overwrideAvailable) {
        originX += x;
        originY += y;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (backgroundTexture != null) {
            RenderSystem.setShaderTexture(0, backgroundTexture);
            GuiComponent.blit(poseStack, originX - (backgroundSizeX / 2), originY - (backgroundSizeY / 2), 0, 0,
                    backgroundSizeX,
                    backgroundSizeY, backgroundTextureSizeX, backgroundTextureSizeY);
        }
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
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
        if (backgroundTexture == null) {
            parent.blit(poseStack, x, y, 24, 22, 29, 24);
        }
        GuiSlot guiSlot = slots.get(listId);
        if (guiSlot.selected().get()) {
            itemRenderList = lateRenderList;
            lateRenderList.add(() -> {
                float blit = parent.getBlitOffset();
                parent.setBlitOffset((int) this.itemRenderer.blitOffset);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, SELECTION_LOCATION);
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

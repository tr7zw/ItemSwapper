package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class ItemGridWidget implements GuiWidget {

    private static final ResourceLocation BACKGROUND_3_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_3_row.png");
    private static final ResourceLocation BACKGROUND_4_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_4_nocenter.png");
    private static final ResourceLocation BACKGROUND_5_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_5_plus.png");
    private static final ResourceLocation BACKGROUND_6_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_6_nocenter.png");
    private static final ResourceLocation BACKGROUND_7_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_7.png");
    private static final ResourceLocation BACKGROUND_8_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_8_nocenter.png");
    private static final ResourceLocation BACKGROUND_9_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_9.png");
    private static final ResourceLocation BACKGROUND_10_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_10.png");
    private static final ResourceLocation BACKGROUND_11_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_11.png");
    private static final ResourceLocation BACKGROUND_12_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_12_nocenter.png");
    private static final ResourceLocation BACKGROUND_13_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_13.png");
    private static final ResourceLocation BACKGROUND_14_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_14.png");
    private static final ResourceLocation BACKGROUND_15_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_15_nocenter.png");
    private static final ResourceLocation BACKGROUND_16_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_16_nocenter.png");
    private static final ResourceLocation BACKGROUND_18_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_18_half_aligned.png");
    private static final ResourceLocation BACKGROUND_20_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_20_nocenter.png");
    private static final ResourceLocation BACKGROUND_22_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_22_half_aligned.png");
    private static final ResourceLocation BACKGROUND_24_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_24_nocenter.png");
    private static final ResourceLocation BACKGROUND_25_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_25_nocenter.png");
    
    protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    protected static final ResourceLocation SELECTION_LOCATION = new ResourceLocation("itemswapper",
                "textures/gui/selection.png");
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

    protected void setupDynamicSlots(int length) {
        if (length <= 3) {
            setupSlots(3, 1, false, BACKGROUND_3_LOCATION);
        } else if (length <= 4) {
            setupSlots(2, 2, false, BACKGROUND_4_LOCATION);
        } else if (length <= 5) {
            setupSlots(3, 3, true, BACKGROUND_5_LOCATION);
        } else if (length <= 6) {
            setupSlots(3, 2, false, BACKGROUND_6_LOCATION);
        } else if (length <= 7) {
            setupHalfGridSlots(3, 3, BACKGROUND_7_LOCATION);
        } else if (length <= 8) {
            setupSlots(4, 2, false, BACKGROUND_8_LOCATION);
        } else if (length <= 9) {
            setupSlots(3, 3, false, BACKGROUND_9_LOCATION);
        } else if (length <= 10) {
            setupHalfGridSlots(4, 3, BACKGROUND_10_LOCATION);
        } else if (length <= 11) {
            setupHalfGridSlots(4, 3, BACKGROUND_11_LOCATION, true);
        } else if (length <= 12) {
            setupSlots(4, 4, true, BACKGROUND_12_LOCATION);
        } else if (length <= 13) {
            setupHalfGridSlots(5, 3, BACKGROUND_13_LOCATION);
        } else if (length <= 14) {
            setupSlots(6, 3, true, BACKGROUND_14_LOCATION);
        } else if (length <= 15) {
            setupSlots(5, 3, false, BACKGROUND_15_LOCATION);
        } else if (length <= 16) {
            setupSlots(4, 4, false, BACKGROUND_16_LOCATION);
        } else if (length <= 18) {
            setupHalfGridSlots(5, 4, BACKGROUND_18_LOCATION);
        } else if (length <= 20) {
            setupSlots(6, 4, true, BACKGROUND_20_LOCATION);
        } else if (length <= 22) {
            setupHalfGridSlots(6, 4, BACKGROUND_22_LOCATION);
        } else if (length <= 24) {
            setupSlots(6, 4, false, BACKGROUND_24_LOCATION);
        } else {
            setupSlots(5, 5, false, BACKGROUND_25_LOCATION);
        }
    }
    
    protected void setupSlots(int width, int height, boolean skipCorners, ResourceLocation texture) {
        widgetArea.setBackgroundTexture(texture);
        widgetArea.setBackgroundSizeX(width * XTOverlay.tinySlotSize + 6);
        widgetArea.setBackgroundSizeY(height * XTOverlay.tinySlotSize + 6);
        int sz = texture == null ? XTOverlay.slotSize : XTOverlay.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        widgetArea.setMouseBoundsX(width * lz);
        widgetArea.setMouseBoundsY(height * lz);
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
    
    protected void setupHalfGridSlots(int width, int height, ResourceLocation texture) {
        setupHalfGridSlots(width, height, texture, false);
    }

    protected void setupHalfGridSlots(int width, int height, ResourceLocation texture, boolean flip) {
        widgetArea.setBackgroundTexture(texture);
        widgetArea.setBackgroundSizeX(width * XTOverlay.tinySlotSize + 6);
        widgetArea.setBackgroundSizeY(height * XTOverlay.tinySlotSize + 6);
        int sz = texture == null ? XTOverlay.slotSize : XTOverlay.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        widgetArea.setMouseBoundsX(width * lz);
        widgetArea.setMouseBoundsY(height * lz);
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = (x == width - 1 && y == height - 1)
                        || (x == width - 1 && y == 0);
                boolean needsOffset = y == 0 || y == height - 1;
                if (flip) {
                    skip = (x == width - 1 && y != height - 1 && y != 0);
                    needsOffset = !needsOffset;
                }
                int xOffset = needsOffset ? sz / 2 : 0;
                if (!skip) {
                    slots.add(new GuiSlot(originX + xOffset + x * sz, originY + y * sz, slotId++, XTOverlay.tinySlotSize));
                }
            }
        }
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
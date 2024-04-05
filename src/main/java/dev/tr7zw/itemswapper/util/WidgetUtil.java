package dev.tr7zw.itemswapper.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.itemswapper.ItemSwapperUI;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.overlay.logic.GuiSlot;
import dev.tr7zw.itemswapper.overlay.logic.WidgetArea;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class WidgetUtil {

    @Deprecated // for <= 1.20.1
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = new ResourceLocation("hud/hotbar_offhand_left");
    public static final ResourceLocation CURSOR_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/cursor.png");
    public static final ResourceLocation SELECTION_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/selection.png");
    public static final ResourceLocation BACKGROUND_3_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_3_row.png");
    public static final ResourceLocation BACKGROUND_4_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_4_nocenter.png");
    public static final ResourceLocation BACKGROUND_5_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_5_plus.png");
    public static final ResourceLocation BACKGROUND_6_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_6_nocenter.png");
    public static final ResourceLocation BACKGROUND_7_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_7.png");
    public static final ResourceLocation BACKGROUND_8_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_8_nocenter.png");
    public static final ResourceLocation BACKGROUND_9_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_9.png");
    public static final ResourceLocation BACKGROUND_10_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_10.png");
    public static final ResourceLocation BACKGROUND_11_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_11.png");
    public static final ResourceLocation BACKGROUND_12_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_12_nocenter.png");
    public static final ResourceLocation BACKGROUND_13_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_13.png");
    public static final ResourceLocation BACKGROUND_14_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_14.png");
    public static final ResourceLocation BACKGROUND_15_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_15_nocenter.png");
    public static final ResourceLocation BACKGROUND_16_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_16_nocenter.png");
    public static final ResourceLocation BACKGROUND_18_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_18_half_aligned.png");
    public static final ResourceLocation BACKGROUND_20_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_20_nocenter.png");
    public static final ResourceLocation BACKGROUND_22_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_22_half_aligned.png");
    public static final ResourceLocation BACKGROUND_24_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_24_nocenter.png");
    public static final ResourceLocation BACKGROUND_25_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_25_nocenter.png");

    private WidgetUtil() {
        // hiden constructor
    }

    public static void renderBackground(WidgetArea widgetArea, RenderContext graphics, int originX, int originY) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (widgetArea.getBackgroundTexture() != null) {
            graphics.blit(widgetArea.getBackgroundTexture(), originX - (widgetArea.getBackgroundSizeX() / 2),
                    originY - (widgetArea.getBackgroundSizeY() / 2), 0, 0, widgetArea.getBackgroundSizeX(),
                    widgetArea.getBackgroundSizeY(), widgetArea.getBackgroundTextureSizeX(),
                    widgetArea.getBackgroundTextureSizeY());
        }
    }

    public static void setupDynamicSlots(WidgetArea widgetArea, List<GuiSlot> slots, int length) {
        if (length <= 1) {
            setupSlots(widgetArea, slots, 1, 1, false, null);
        } else if (length <= 2) {
            setupSlots(widgetArea, slots, 2, 1, false, null);
        } else if (length <= 3) {
            setupSlots(widgetArea, slots, 3, 1, false, BACKGROUND_3_LOCATION);
        } else if (length <= 4) {
            setupSlots(widgetArea, slots, 2, 2, false, BACKGROUND_4_LOCATION);
        } else if (length <= 5) {
            setupSlots(widgetArea, slots, 3, 3, true, BACKGROUND_5_LOCATION);
        } else if (length <= 6) {
            setupSlots(widgetArea, slots, 3, 2, false, BACKGROUND_6_LOCATION);
        } else if (length <= 7) {
            setupHalfGridSlots(widgetArea, slots, 3, 3, BACKGROUND_7_LOCATION);
        } else if (length <= 8) {
            setupSlots(widgetArea, slots, 4, 2, false, BACKGROUND_8_LOCATION);
        } else if (length <= 9) {
            setupSlots(widgetArea, slots, 3, 3, false, BACKGROUND_9_LOCATION);
        } else if (length <= 10) {
            setupHalfGridSlots(widgetArea, slots, 4, 3, BACKGROUND_10_LOCATION);
        } else if (length <= 11) {
            setupHalfGridSlots(widgetArea, slots, 4, 3, BACKGROUND_11_LOCATION, true);
        } else if (length <= 12) {
            setupSlots(widgetArea, slots, 4, 4, true, BACKGROUND_12_LOCATION);
        } else if (length <= 13) {
            setupHalfGridSlots(widgetArea, slots, 5, 3, BACKGROUND_13_LOCATION);
        } else if (length <= 14) {
            setupSlots(widgetArea, slots, 6, 3, true, BACKGROUND_14_LOCATION);
        } else if (length <= 15) {
            setupSlots(widgetArea, slots, 5, 3, false, BACKGROUND_15_LOCATION);
        } else if (length <= 16) {
            setupSlots(widgetArea, slots, 4, 4, false, BACKGROUND_16_LOCATION);
        } else if (length <= 18) {
            setupHalfGridSlots(widgetArea, slots, 5, 4, BACKGROUND_18_LOCATION);
        } else if (length <= 20) {
            setupSlots(widgetArea, slots, 6, 4, true, BACKGROUND_20_LOCATION);
        } else if (length <= 22) {
            setupHalfGridSlots(widgetArea, slots, 6, 4, BACKGROUND_22_LOCATION);
        } else if (length <= 24) {
            setupSlots(widgetArea, slots, 6, 4, false, BACKGROUND_24_LOCATION);
        } else {
            setupSlots(widgetArea, slots, 5, 5, false, BACKGROUND_25_LOCATION);
        }
    }

    public static void setupSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            boolean skipCorners, ResourceLocation texture) {
        widgetArea.setBackgroundTexture(texture);
        widgetArea.setBackgroundSizeX(width * ItemSwapperUI.tinySlotSize + 6);
        widgetArea.setBackgroundSizeY(height * ItemSwapperUI.tinySlotSize + 6);
        int sz = texture == null ? ItemSwapperUI.slotSize : ItemSwapperUI.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        widgetArea.setMouseBoundsX(width * lz);
        widgetArea.setMouseBoundsY(height * lz);
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = skipCorners && ((x == 0 && y == 0) || (x == 0 && y == height - 1)
                        || (x == width - 1 && y == height - 1) || (x == width - 1 && y == 0));
                if (!skip) {
                    slots.add(new GuiSlot(originX + x * sz, originY + y * sz, slotId, ItemSwapperUI.tinySlotSize));
                    slotId++;
                }
            }
        }
    }

    public static void setupHalfGridSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            ResourceLocation texture) {
        setupHalfGridSlots(widgetArea, slots, width, height, texture, false);
    }

    public static void setupHalfGridSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            ResourceLocation texture, boolean flip) {
        widgetArea.setBackgroundTexture(texture);
        widgetArea.setBackgroundSizeX(width * ItemSwapperUI.tinySlotSize + 6);
        widgetArea.setBackgroundSizeY(height * ItemSwapperUI.tinySlotSize + 6);
        int sz = texture == null ? ItemSwapperUI.slotSize : ItemSwapperUI.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        widgetArea.setMouseBoundsX(width * lz);
        widgetArea.setMouseBoundsY(height * lz);
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = (x == width - 1 && y == height - 1) || (x == width - 1 && y == 0);
                boolean needsOffset = y == 0 || y == height - 1;
                if (flip) {
                    skip = (x == width - 1 && y != height - 1 && y != 0);
                    needsOffset = !needsOffset;
                }
                int xOffset = needsOffset ? sz / 2 : 0;
                if (!skip) {
                    slots.add(new GuiSlot(originX + xOffset + x * sz, originY + y * sz, slotId++,
                            ItemSwapperUI.tinySlotSize));
                }
            }
        }
    }

}

package dev.tr7zw.itemswapper.util;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;

import java.util.List;

import dev.tr7zw.itemswapper.ItemSwapperUI;
import dev.tr7zw.itemswapper.overlay.logic.GuiSlot;
import dev.tr7zw.itemswapper.overlay.logic.WidgetArea;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.resources.*;

public class WidgetUtil {
    @Deprecated // for <= 1.20.1
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ WIDGETS_LOCATION = getResourceLocation(
            "textures/gui/widgets.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ HOTBAR_OFFHAND_LEFT_SPRITE = getResourceLocation(
            "hud/hotbar_offhand_left");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ CURSOR_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/cursor.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ SELECTION_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/selection.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_3_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_3_row.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_4_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_4_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_5_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_5_plus.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_6_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_6_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_7_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_7.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_8_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_8_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_9_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_9.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_10_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_10.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_11_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_11.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_12_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_12_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_13_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_13.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_14_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_14.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_15_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_15_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_16_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_16_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_18_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_18_half_aligned.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_20_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_20_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_22_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_22_half_aligned.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_24_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_24_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_25_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_25_nocenter.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_28_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_28.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_30_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_30.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_33_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_33.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_35_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_35.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_38_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_38.png");
    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_40_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/inv_wheel_40.png");

    private WidgetUtil() {
        // hiden constructor
    }

    public static void renderBackground(WidgetArea widgetArea, RenderContext graphics, int originX, int originY) {
        //? if < 1.21.6 {
        /*
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///? }
           //? if >= 1.21.5 {

        //? } else if >= 1.21.2 {

        // com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        // com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX);
        //? } else {
        /*
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem
                .setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        *///? }
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
        } else if (length <= 25) {
            setupSlots(widgetArea, slots, 5, 5, false, BACKGROUND_25_LOCATION);
        } else if (length <= 28) {
            setupSlots(widgetArea, slots, new int[]{3, 7, 8, 7, 3}, BACKGROUND_28_LOCATION);
        } else if (length <= 30) {
            setupSlots(widgetArea, slots, new int[]{6, 6, 6, 6, 6, 6}, BACKGROUND_30_LOCATION);
        } else if (length <= 33) {
            setupSlots(widgetArea, slots, new int[]{6, 7, 7, 7, 6}, BACKGROUND_33_LOCATION);
        } else if (length <= 35) {
            setupSlots(widgetArea, slots, new int[]{6, 7, 9, 7, 6}, BACKGROUND_35_LOCATION);
        } else if (length <= 38) {
            setupSlots(widgetArea, slots, new int[]{6, 8, 9, 8, 6}, BACKGROUND_38_LOCATION);
        } else {
            setupSlots(widgetArea, slots, new int[]{8, 8, 8, 8, 8}, BACKGROUND_40_LOCATION);
        }
    }

    public static void setupSlots(WidgetArea widgetArea, List<GuiSlot> slots, int[] lines, /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ texture) {
        int maxWidth = 0;
        for (int line : lines) {
            if (line > maxWidth) {
                maxWidth = line;
            }
        }
        widgetArea.setBackgroundTexture(texture);
        widgetArea.setBackgroundSizeX(maxWidth * ItemSwapperUI.tinySlotSize + 6);
        widgetArea.setBackgroundSizeY(lines.length * ItemSwapperUI.tinySlotSize + 6);
        widgetArea.setBackgroundTextureSizeX(maxWidth >= 7 ? 256 : 128);
        int sz = texture == null ? ItemSwapperUI.slotSize : ItemSwapperUI.tinySlotSize;
        int lz = texture == null ? 11 : 9;
        widgetArea.setMouseBoundsX(maxWidth * lz);
        widgetArea.setMouseBoundsY(lines.length * lz);
        int originX = (int) (-maxWidth / 2d * sz - 2);
        int originY = (int) (-lines.length / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < lines.length; y++) {
            int xOffset = (maxWidth - lines[y]) % 2 == 1 ? sz / 2 : 0;
            for (int x = 0; x < lines[y]; x++) {
                slots.add(new GuiSlot(originX + xOffset + ((maxWidth - lines[y]) / 2 + x) * sz, originY + y * sz, slotId, ItemSwapperUI.tinySlotSize));
                slotId++;
            }
        }
    }

    public static void setupSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            boolean skipCorners, /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ texture) {
        int[] size = new int[height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = skipCorners && ((x == 0 && y == 0) || (x == 0 && y == height - 1)
                        || (x == width - 1 && y == height - 1) || (x == width - 1 && y == 0));
                if (!skip) {
                    size[y]++;
                }
            }
        }
        setupSlots(widgetArea, slots, size, texture);
    }

    public static void setupHalfGridSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ texture) {
        setupHalfGridSlots(widgetArea, slots, width, height, texture, false);
    }

    public static void setupHalfGridSlots(WidgetArea widgetArea, List<GuiSlot> slots, int width, int height,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ texture, boolean flip) {
        int[] size = new int[height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = (x == width - 1 && y == height - 1) || (x == width - 1 && y == 0);
                if (!skip) {
                    size[y]++;
                }
            }
        }
        setupSlots(widgetArea, slots, size, texture);
    }

}

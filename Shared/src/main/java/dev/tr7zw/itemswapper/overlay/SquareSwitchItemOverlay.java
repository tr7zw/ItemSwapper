package dev.tr7zw.itemswapper.overlay;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import net.minecraft.resources.ResourceLocation;

public class SquareSwitchItemOverlay extends SwitchItemOverlay {

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

    public SquareSwitchItemOverlay(ItemGroup itemGroup) {
        super(itemGroup);
    }

    public void setupSlots() {
        int length = getItemGroup().getItems().length;
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

    private void setupHalfGridSlots(int width, int height, ResourceLocation texture) {
        setupHalfGridSlots(width, height, texture, false);
    }

    private void setupHalfGridSlots(int width, int height, ResourceLocation texture, boolean flip) {
        setBackgroundTexture(texture);
        setBackgroundSizeX(width * tinySlotSize + 6);
        setBackgroundSizeY(height * tinySlotSize + 6);
        int sz = texture == null ? slotSize : tinySlotSize;
        int lz = texture == null ? 11 : 9;
        setLimitX(width * lz);
        setLimitY(height * lz);
        setDeadZone(0);
        int slotAmount = width * height - 2;
        if (flip) {
            slotAmount++;
        }
        setGuiSlots(new GuiSlot[slotAmount]);
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
                    getGuiSlots()[slotId++] = new GuiSlot(originX + xOffset + x * sz, originY + y * sz);
                }
            }
        }
    }

}

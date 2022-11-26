package dev.tr7zw.itemswapper.overlay;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class RoundSwitchItemOverlay extends SwitchItemOverlay {

    private static final ResourceLocation BACKGROUND_8_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_8.png");
    private static final ResourceLocation BACKGROUND_12_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_12.png");
    private static final ResourceLocation BACKGROUND_16_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_16.png");
    private static final ResourceLocation BACKGROUND_20_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_20.png");

    public RoundSwitchItemOverlay(Item[] selection) {
        super(selection);
    }

    public void setupSlots() {
        if (getItemSelection().length <= 8) {
            setup8Slots();
            return;
        }
        if (getItemSelection().length <= 12) {
            setup12Slots();
            return;
        }
        if (getItemSelection().length <= 16) {
            setup16Slots();
            return;
        }
        setup20Slots();
    }

    private void setup8Slots() {
        setBackgroundTexture(BACKGROUND_8_LOCATION);
        setBackgroundSizeX(60);
        setBackgroundSizeY(60);
        setLimitX(33);
        setLimitY(33);
        setDeadZone(11);
        setGuiSlots(new GuiSlot[8]);
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            getGuiSlots()[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                getGuiSlots()[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
    }

    private void setup12Slots() {
        setBackgroundTexture(BACKGROUND_12_LOCATION);
        setBackgroundSizeX(96);
        setBackgroundSizeY(96);
        setLimitX(44);
        setLimitY(33);
        setDeadZone(11);
        setGuiSlots(new GuiSlot[12]);
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            getGuiSlots()[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                getGuiSlots()[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 2; i++) {
            getGuiSlots()[i * 2 + 8] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
            getGuiSlots()[i * 2 + 9] = new GuiSlot(originX + 3 * tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
        }
    }

    
    private void setup16Slots() {
        setBackgroundTexture(BACKGROUND_16_LOCATION);
        setBackgroundSizeX(96);
        setBackgroundSizeY(96);
        setLimitX(44);
        setLimitY(44);
        setDeadZone(11);
        setGuiSlots(new GuiSlot[16]);
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            getGuiSlots()[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                getGuiSlots()[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 2; i++) {
            getGuiSlots()[i * 2 + 8] = new GuiSlot(originX + i * tinySlotSize + tinySlotSize / 2, originY - tinySlotSize);
            getGuiSlots()[i * 2 + 9] = new GuiSlot(originX + i * tinySlotSize + tinySlotSize / 2, originY + tinySlotSize * 3);
        }
        for (int i = 0; i < 2; i++) {
            getGuiSlots()[i * 2 + 12] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
            getGuiSlots()[i * 2 + 13] = new GuiSlot(originX + 3 * tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
        }
    }

    private void setup20Slots() {
        setBackgroundTexture(BACKGROUND_20_LOCATION);
        setBackgroundSizeX(96);
        setBackgroundSizeY(96);
        setLimitX(44);
        setLimitY(44);
        setDeadZone(11);
        setGuiSlots(new GuiSlot[20]);
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            getGuiSlots()[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                getGuiSlots()[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[8 + i] = new GuiSlot(originX + i * tinySlotSize, originY - tinySlotSize);
            getGuiSlots()[8 + i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 3);
        }
        for (int i = 0; i < 3; i++) {
            getGuiSlots()[14 + i] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize);
            getGuiSlots()[14 + i + 3] = new GuiSlot(originX + tinySlotSize * 3, originY + i * tinySlotSize);
        }
    }

}

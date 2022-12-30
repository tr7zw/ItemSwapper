package dev.tr7zw.itemswapper.overlay;

import net.minecraft.client.gui.screens.Overlay;

public abstract class XTOverlay extends Overlay {

    public static final int slotSize = 22;
    public static final int tinySlotSize = 18;

    public abstract void handleInput(double x, double y);

    public abstract void handleSwitchSelection();

    public abstract void onClose();

    public boolean lockMouse() {
        return true;
    }

    public void onScroll(double signum) {
        // nothing
    }

}

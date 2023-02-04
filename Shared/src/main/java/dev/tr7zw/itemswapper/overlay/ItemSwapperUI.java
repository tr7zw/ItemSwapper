package dev.tr7zw.itemswapper.overlay;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;

public interface ItemSwapperUI {

    public static final int slotSize = 22;
    public static final int tinySlotSize = 18;

    public abstract void handleInput(double x, double y);

    public abstract void handleSwitchSelection();

    public default void close() {
        ItemSwapperSharedMod.closeScreen(this);
    }

    public abstract void onOverlayClose();

    public default boolean lockMouse() {
        return true;
    }

    public default void onScroll(double signum) {
        // nothing
    }

}

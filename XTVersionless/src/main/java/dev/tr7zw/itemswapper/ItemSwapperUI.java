package dev.tr7zw.itemswapper;

public interface ItemSwapperUI {

    public static final int slotSize = 22;
    public static final int tinySlotSize = 18;

    public abstract void handleInput(double x, double y);

    public abstract void onSecondaryClick();

    /**
     * @return true if the UI should stay open
     */
    public abstract boolean onPrimaryClick();

    public default boolean lockMouse() {
        return true;
    }

    public default void onScroll(double signum) {
        // nothing
    }

}

package dev.tr7zw.itemswapper.compat;

import dev.tr7zw.itemswapper.ItemSwapperBase;

public class ControlifySupport {

    private static final ControlifySupport INSTANCE = new ControlifySupport();
    private boolean isAvailable = false;

    private ControlifySupport() {
    }

    public void init() {
        isAvailable = true;

        ItemSwapperBase.LOGGER.info("Controlify support enabled!");
    }

    public static ControlifySupport getInstance() {
        return INSTANCE;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isActive() {
        return isAvailable();
    }

}

package dev.tr7zw.itemswapper.overlay;

import net.minecraft.client.gui.screens.Overlay;

public abstract class XTOverlay extends Overlay {

    public abstract void handleInput(double x, double y);
    
    public abstract void handleSwitchSelection();
    
    public abstract void onClose();

    public void onScroll(double signum) {
        // nothing
    }
    
}

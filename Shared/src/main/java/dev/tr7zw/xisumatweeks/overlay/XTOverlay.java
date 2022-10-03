package dev.tr7zw.xisumatweeks.overlay;

import net.minecraft.client.gui.screens.Overlay;

public abstract class XTOverlay extends Overlay {

    public abstract void handleInput(double x, double y);
    
    public abstract void handleSwitchSelection();
    
    public abstract void onClose();
    
}

package dev.tr7zw.itemswapper.overlay;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ItemSwapperUIAbstractInput extends Screen implements ItemSwapperUI {

    protected ItemSwapperUIAbstractInput(Component component) {
        super(component);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(i == 0) {
            close();
        } else if(i == 1 || i == 2) {
            handleSwitchSelection();
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        onScroll(f);
        return true;
    }
    
}

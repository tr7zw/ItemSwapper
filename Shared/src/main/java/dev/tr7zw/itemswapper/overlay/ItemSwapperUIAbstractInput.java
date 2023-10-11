package dev.tr7zw.itemswapper.overlay;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ItemSwapperUIAbstractInput extends Screen implements ItemSwapperUI {

    protected ItemSwapperUIAbstractInput(Component component) {
        super(component);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(i == 0) {
            ItemSwapperSharedMod.onPrimaryClick(this, false);
        } else if(i == 1 || i == 2) {
            onSecondaryClick();
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        onScroll(g);
        return true;
    }
    
}

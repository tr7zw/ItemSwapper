package dev.tr7zw.itemswapper.manager.itemgroups;

import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.network.chat.Component;

public interface Shortcut {

    public Icon getIcon();

    /**
     * 
     * @param action
     * @return true if the UI should be kept open if possible
     */
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset);

    public default boolean acceptPrimaryClick() {
        return true;
    }

    public default boolean acceptSecondaryClick() {
        return true;
    }

    public default Component getHoverText() {
        return null;
    }

    public default boolean isVisible() {
        return true;
    }

    public String getSelector();

    public enum ActionType {
        SECONDARY_CLICK, PRIMARY_CLICK
    }

}

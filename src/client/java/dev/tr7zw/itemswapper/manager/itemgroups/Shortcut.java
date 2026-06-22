package dev.tr7zw.itemswapper.manager.itemgroups;

import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.network.chat.Component;

public interface Shortcut {

    Icon getIcon();

    /**
     * 
     * @param action
     * @return true if the UI should be kept open if possible
     */
    boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset);

    default boolean acceptPrimaryClick() {
        return true;
    }

    default boolean acceptSecondaryClick() {
        return true;
    }

    default Component getHoverText() {
        return null;
    }

    default boolean isVisible() {
        return true;
    }

    String getSelector();

    enum ActionType {
        SECONDARY_CLICK, PRIMARY_CLICK
    }

}

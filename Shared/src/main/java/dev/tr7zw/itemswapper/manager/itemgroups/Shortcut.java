package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;

public interface Shortcut {

    public Icon getIcon();

    /**
     * 
     * @param action
     * @return true if the UI should be kept open if possible
     */
    public boolean invoke(ActionType action);

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

    public enum ActionType {
        SECONDARY_CLICK, PRIMARY_CLICK
    }

}

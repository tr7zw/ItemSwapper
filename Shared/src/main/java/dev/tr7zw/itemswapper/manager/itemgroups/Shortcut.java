package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;

public interface Shortcut {

    public Icon getIcon();

    public void invoke(ActionType action);

    public boolean acceptClose();

    public boolean acceptClick();
    
    public default Component getHoverText() {
        return null;
    }

    public default boolean isVisible() {
        return true;
    }

    public enum ActionType {
        CLICK, CLOSE
    }

}

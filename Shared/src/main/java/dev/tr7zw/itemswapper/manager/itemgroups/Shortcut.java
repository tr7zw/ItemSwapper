package dev.tr7zw.itemswapper.manager.itemgroups;

public interface Shortcut {

    public ItemEntry getIcon();
    
    public void invoke();
    
    public boolean acceptClose();
    
    public boolean acceptClick();
    
    public default boolean isVisible() {
        return true;
    }
    
}

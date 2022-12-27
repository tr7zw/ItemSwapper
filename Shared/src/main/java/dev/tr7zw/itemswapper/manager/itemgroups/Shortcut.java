package dev.tr7zw.itemswapper.manager.itemgroups;

public interface Shortcut {

    public ItemEntry getIcon();
    
    public void invoke();
    
    public boolean acceptLeftclick();
    
    public boolean acceptMiddleclick();
    
}

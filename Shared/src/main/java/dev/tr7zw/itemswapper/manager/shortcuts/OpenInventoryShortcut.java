package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class OpenInventoryShortcut implements Shortcut {
   
    public static final OpenInventoryShortcut INSTANCE = new OpenInventoryShortcut();
    
    private final ItemEntry icon = new ItemEntry(Items.CHEST, null, Component.literal("Open Inventory"));
    
    @Override
    public ItemEntry getIcon() {
        return icon;
    }

    @Override
    public void invoke() {
        ItemSwapperSharedMod.openInventoryScreen();
    }

    @Override
    public boolean acceptClose() {
        return false;
    }

    @Override
    public boolean acceptClick() {
        return true;
    }

}

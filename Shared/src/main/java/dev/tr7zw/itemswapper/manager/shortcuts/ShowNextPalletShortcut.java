package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import net.minecraft.resources.ResourceLocation;

public class ShowNextPalletShortcut implements Shortcut {
    
    private ItemGroupManager manager = ItemSwapperSharedMod.instance.getItemGroupManager();
    private ResourceLocation nextId;
    
    public ShowNextPalletShortcut(ResourceLocation nextId) {
        this.nextId = nextId;
    }
    
    @Override
    public ItemEntry getIcon() {
        return manager.getItemGroup(nextId).getItem(0);
    }

    @Override
    public void invoke() {
        ItemSwapperSharedMod.instance.openSquareSwitchScreen(manager.getItemGroup(nextId));
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

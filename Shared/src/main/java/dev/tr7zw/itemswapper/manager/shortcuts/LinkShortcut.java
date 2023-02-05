package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.NoPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class LinkShortcut implements Shortcut {

    private ItemGroupManager manager = ItemSwapperSharedMod.instance.getItemGroupManager();
    private ResourceLocation nextId;
    private Component nameOverwrite = null;

    public LinkShortcut(ResourceLocation nextId) {
        this.nextId = nextId;
    }

    public LinkShortcut(ResourceLocation nextId, Component nameOverwrite) {
        this.nextId = nextId;
        this.nameOverwrite = nameOverwrite;
    }

    @Override
    public ItemEntry getIcon() {
        Page page = manager.getPage(nextId);
        if (page instanceof ItemGroupPage group) {
            Component displayName = null;
            if(nameOverwrite != null) {
                displayName = nameOverwrite;
            } else if(group.group().getDisplayName() != null) {
                displayName = group.group().getDisplayName();
            }
            return new ItemEntry(group.group().getItem(0).getItem(), null, displayName);
        } else if (page instanceof ListPage list) {
            return new ItemEntry(list.items()[0], null, nameOverwrite);
        }
        return new ItemEntry(Items.AIR, null);
    }

    @Override
    public void invoke(ActionType action) {
        ItemSwapperSharedMod.instance.openPage(manager.getPage(nextId));
    }

    @Override
    public boolean acceptClose() {
        return false;
    }

    @Override
    public boolean acceptClick() {
        return true;
    }

    @Override
    public boolean isVisible() {
        Page page = manager.getPage(nextId);
        return page != null && !(page instanceof NoPage);
    }

}

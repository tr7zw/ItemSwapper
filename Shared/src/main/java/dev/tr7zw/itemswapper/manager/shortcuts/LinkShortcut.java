package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.NoPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class LinkShortcut implements Shortcut {

    private ItemGroupManager manager = ItemSwapperSharedMod.instance.getItemGroupManager();
    private ResourceLocation nextId;
    private Component fallbackName = null;

    public LinkShortcut(ResourceLocation nextId) {
        this.nextId = nextId;
    }

    public LinkShortcut(ResourceLocation nextId, Component fallbackName) {
        this.nextId = nextId;
        this.fallbackName = fallbackName;
    }

    @Override
    public Icon getIcon() {
        Page page = manager.getPage(nextId);
        if (page instanceof ItemGroupPage group) {
            Component displayName = null;
            if (group.group().getDisplayName() != null) {
                displayName = group.group().getDisplayName();
            } else if (fallbackName != null) {
                displayName = fallbackName;
            }
            return new ItemIcon(group.group().getItem(0).getItem().getDefaultInstance(), displayName);
        } else if (page instanceof ListPage list) {
            Component displayName = null;
            if (list.items().getDisplayName() != null) {
                displayName = list.items().getDisplayName();
            } else if (fallbackName != null) {
                displayName = fallbackName;
            }
            return new ItemIcon(list.items().getItems()[0].getDefaultInstance(), displayName);
        }
        return new ItemIcon(Items.AIR.getDefaultInstance(), Component.empty());
    }

    @Override
    public boolean invoke(ActionType action) {
        ItemSwapperSharedMod.instance.openPage(manager.getPage(nextId));
        return true;
    }

    @Override
    public boolean isVisible() {
        Page page = manager.getPage(nextId);
        return page != null && !(page instanceof NoPage);
    }

}

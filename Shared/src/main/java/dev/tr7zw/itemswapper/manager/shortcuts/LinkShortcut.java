package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.NoPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.LinkIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LinkShortcut implements Shortcut {

    private final ItemGroupManager manager = ItemSwapperSharedMod.instance.getItemGroupManager();
    private final ResourceLocation nextId;
    private final Component displayName;
    private final Item displayIcon;

    public LinkShortcut(ResourceLocation nextId) {
        this.nextId = nextId;
        this.displayName = null;
        this.displayIcon = null;
    }

    public LinkShortcut(ResourceLocation nextId, Component displayName, Item icon) {
        this.nextId = nextId;
        this.displayName = displayName;
        this.displayIcon = icon;
    }

    @Override
    public Icon getIcon() {
        Page page = manager.getPage(nextId);
        if (page instanceof ItemGroupPage group) {
            Component name = null;
            if (displayName != null) {
                name = displayName;
            } else if (group.group().getDisplayName() != null) {
                name = group.group().getDisplayName();
            }
            ItemStack icon = group.group().getItem(0).getItem().getDefaultInstance();
            if (displayIcon != null && displayIcon != Items.AIR) {
                icon = displayIcon.getDefaultInstance();
            } else if (group.group().getIcon() != null && group.group().getIcon() != Items.AIR) {
                icon = group.group().getIcon().getDefaultInstance();
            }
            return new LinkIcon(icon, name, nextId);
        } else if (page instanceof ListPage list) {
            Component name = null;
            if (displayName != null) {
                name = displayName;
            } else if (list.items().getDisplayName() != null) {
                name = list.items().getDisplayName();
            }
            ItemStack icon = list.items().getItems()[0].getDefaultInstance();
            if (displayIcon != null && displayIcon != Items.AIR) {
                icon = displayIcon.getDefaultInstance();
            } else if (list.items().getIcon() != null && list.items().getIcon() != Items.AIR) {
                icon = list.items().getIcon().getDefaultInstance();
            }
            return new LinkIcon(icon, name, nextId);
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

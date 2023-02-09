package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class LastItemShortcut implements Shortcut {

    private Item lastItem;
    private Page lastPage;
    private Component displayName = null;

    public LastItemShortcut(Item lastItem, Page lastPage, Component displayName) {
        this.lastItem = lastItem;
        this.lastPage = lastPage;
        this.displayName = displayName;
    }

    @Override
    public Icon getIcon() {
        return new ItemIcon(lastItem.getDefaultInstance(), displayName);
    }

    @Override
    public void invoke(ActionType action) {
        if(action == ActionType.CLICK) {
            ItemSwapperSharedMod.instance.openPage(lastPage);
        } else {
            ItemUtil.grabItem(lastItem, true);
        }
        
    }

    @Override
    public boolean acceptClose() {
        return true;
    }

    @Override
    public boolean acceptClick() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return lastItem != null && lastPage != null && !Minecraft.getInstance().player.isCreative();
    }

}

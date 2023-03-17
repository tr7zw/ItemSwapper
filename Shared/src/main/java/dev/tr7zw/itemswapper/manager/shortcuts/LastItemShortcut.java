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
    private Component displayName = Component.translatable("text.itemswapper.lastItem");
    private final Component hoverText = Component.translatable("text.itemswapper.lastItem.tooltip");

    public LastItemShortcut(Item lastItem, Page lastPage) {
        this.lastItem = lastItem;
        this.lastPage = lastPage;
    }

    @Override
    public Icon getIcon() {
        return new ItemIcon(lastItem.getDefaultInstance(), displayName);
    }

    @Override
    public boolean invoke(ActionType action) {
        if(action == ActionType.SECONDARY_CLICK) {
            ItemSwapperSharedMod.instance.openPage(lastPage);
            return true;
        } else {
            ItemUtil.grabItem(lastItem, true);
            return false;
        }
    }

    @Override
    public boolean isVisible() {
        return lastItem != null && lastPage != null && !Minecraft.getInstance().player.isCreative();
    }
    
    @Override
    public Component getHoverText() {
        return hoverText;
    }

}

package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class LastItemShortcut implements Shortcut {

    private Item lastItem;
    private Page lastPage;
    private Component displayName = ComponentProvider.translatable("text.itemswapper.lastItem");
    private final Component hoverText = ComponentProvider.translatable("text.itemswapper.lastItem.tooltip");

    public LastItemShortcut(Item lastItem, Page lastPage) {
        this.lastItem = lastItem;
        this.lastPage = lastPage;
    }

    @Override
    public Icon getIcon() {
        return new ItemIcon(lastItem.getDefaultInstance(), displayName);
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        if (action == ActionType.SECONDARY_CLICK) {
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

    @Override
    public String getSelector() {
        return "lastItem";
    }

}

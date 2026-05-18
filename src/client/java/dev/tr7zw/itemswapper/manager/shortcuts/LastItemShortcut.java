package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public record LastItemShortcut(Item lastItem, Page lastPage) implements Shortcut {

    private static final Component displayName = ComponentProvider.translatable("text.itemswapper.lastItem");
    private static final Component hoverText = ComponentProvider.translatable("text.itemswapper.lastItem.tooltip");

    @Override
    public Icon getIcon() {
        return new ItemIcon(lastItem.getDefaultInstance(), displayName);
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        if (action == ActionType.SECONDARY_CLICK) {
            ItemSwapperSharedMod.instance.getClientUiManager().openPage(lastPage);
            return true;
        } else {
            ItemSwapperSharedMod.instance.getItemManager().grabItem(lastItem, true);
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

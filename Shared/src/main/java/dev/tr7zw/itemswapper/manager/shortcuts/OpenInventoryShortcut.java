package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.InventoryPage;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class OpenInventoryShortcut implements Shortcut {

    private final ItemEntry icon = new ItemEntry(Items.CHEST, null, Component.literal("Open Inventory"));
    private final SwitchItemOverlay overlay;

    public OpenInventoryShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public ItemEntry getIcon() {
        return icon;
    }

    @Override
    public void invoke(ActionType action) {
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

    @Override
    public boolean isVisible() {
        return !(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1) instanceof InventoryPage);
    }

}

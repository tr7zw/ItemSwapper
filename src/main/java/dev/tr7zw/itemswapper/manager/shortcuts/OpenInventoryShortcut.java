package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.InventoryPage;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class OpenInventoryShortcut implements Shortcut {

    private final Icon icon = new ItemIcon(Items.CHEST.getDefaultInstance(),
            ComponentProvider.translatable("text.itemswapper.openInventory"));
    private final SwitchItemOverlay overlay;
    private final Component hoverText = ComponentProvider.translatable("text.itemswapper.openInventory.tooltip");

    public OpenInventoryShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        ItemSwapperSharedMod.openInventoryScreen();
        return true;
    }

    @Override
    public boolean isVisible() {
        return !(overlay.getLastPages().get(overlay.getLastPages().size() - 1) instanceof InventoryPage);
    }

    @Override
    public Component getHoverText() {
        return hoverText;
    }

    @Override
    public String getSelector() {
        return "openInventory";
    }

}

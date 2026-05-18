package dev.tr7zw.itemswapper.manager.shortcuts;

import java.util.List;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.InventoryUtil;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record RestockShortcut() implements Shortcut {

    private static final Icon icon = new ItemIcon(Items.SHULKER_BOX.getDefaultInstance(),
            ComponentProvider.translatable("text.itemswapper.restockAll"));
    private static final Component hoverText = ComponentProvider.translatable("text.itemswapper.restockAll.tooltip");

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(Minecraft.getInstance().player.getInventory());
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int space = item.getMaxStackSize() - item.count();
            if (space > 0) {
                ClientNetworkUtil.sendPacket(new RefillItemPayload(i));
            }
        }
        return true;
    }

    @Override
    public boolean isVisible() {
        return !Minecraft.getInstance().player.isCreative();
    }

    @Override
    public Component getHoverText() {
        return hoverText;
    }

    @Override
    public String getSelector() {
        return "restock";
    }

}

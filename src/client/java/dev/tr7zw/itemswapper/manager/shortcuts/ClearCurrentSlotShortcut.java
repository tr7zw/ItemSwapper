package dev.tr7zw.itemswapper.manager.shortcuts;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.ComponentProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public record ClearCurrentSlotShortcut() implements Shortcut {

    private static final Icon icon = new TextureIcon(getResourceLocation("itemswapper", "textures/gui/clear_slot.png"),
            ComponentProvider.translatable("text.itemswapper.clearSlot"));

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final Component hoverText = ComponentProvider.translatable("text.itemswapper.clearSlot.tooltip");

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        ItemSwapperSharedMod.instance.getItemManager()
                .sendEmptySlotPayload(minecraft.player.getInventory().getSelectedSlot());
        if (action == ActionType.SECONDARY_CLICK) {
            overlay.setHideClearSlotShortcut(true);
            // reopen to re-init the UI
            overlay.openPage(overlay.getLastPages().remove(overlay.getLastPages().size() - 1));
        }
        return true;
    }

    @Override
    public boolean isVisible() {
        return !minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
                && !Minecraft.getInstance().player.isCreative();
    }

    @Override
    public Component getHoverText() {
        return hoverText;
    }

    @Override
    public String getSelector() {
        return "clearSlot";
    }

}

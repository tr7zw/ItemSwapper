package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class ClearCurrentSlotShortcut implements Shortcut {

    private final Icon icon = new TextureIcon(new ResourceLocation("itemswapper", "textures/gui/clear_slot.png"), Component.translatable("text.itemswapper.clearSlot"));
    private final Minecraft minecraft = Minecraft.getInstance();
    private final SwitchItemOverlay overlay;
    private final Component hoverText = Component.translatable("text.itemswapper.clearSlot.tooltip");

    public ClearCurrentSlotShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public void invoke(ActionType action) {
        ItemUtil.grabItem(Items.AIR, true);
        if (action == ActionType.CLICK) {
            overlay.hideClearSlotShortcut = true;
            // reopen to re-init the UI
            overlay.openPage(overlay.getPageHistory().remove(overlay.getPageHistory().size() - 1));
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
        return !minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !Minecraft.getInstance().player.isCreative();
    }
    
    @Override
    public Component getHoverText() {
        return hoverText;
    }


}

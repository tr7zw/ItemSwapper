package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.*;
import dev.tr7zw.itemswapper.overlay.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.client.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

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
        ItemSwapperSharedMod.instance.getItemManager().processRestock();
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

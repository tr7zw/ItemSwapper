package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.TexturePage;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ColorUtil;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;

public class LastItemShortcutTest implements Shortcut {

    private int offset;
    private final Component displayName;
    private final Component hoverText = ComponentProvider.translatable("text.itemswapper.openPalette.tooltip");

    public LastItemShortcutTest(int offset) {
        this.offset = offset;
        this.displayName = ComponentProvider.translatable("text.itemswapper.openPalette." + offset);
    }

    @Override
    public Icon getIcon() {
        return new ItemIcon(Items.PAINTING.getDefaultInstance(), displayName);
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
            UnpackedColor[] color = ItemSwapperSharedMod.instance.getBlockTextureManager()
                    .getColor(blockItem.getBlock());
            ItemSwapperSharedMod.instance.openPage(
                    new TexturePage(new UnpackedColor[] { ColorUtil.createTetradPalette(color[0]).get(offset) }));
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return Minecraft.getInstance().player.getMainHandItem().getItem() instanceof BlockItem blockItem
                && ItemSwapperSharedMod.instance.getBlockTextureManager().getColor(blockItem.getBlock()) != null;
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

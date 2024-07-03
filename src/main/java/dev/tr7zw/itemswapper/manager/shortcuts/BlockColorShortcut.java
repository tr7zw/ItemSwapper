package dev.tr7zw.itemswapper.manager.shortcuts;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.TexturePage;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ColorUtil;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import dev.tr7zw.util.ComponentProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;

public class BlockColorShortcut implements Shortcut {

    private int offset;
    private final UnpackedColor color;
    private final Icon icon;
    private final Component hoverText = ComponentProvider.translatable("text.itemswapper.openPalette.tooltip");

    public BlockColorShortcut(UnpackedColor color, int offset) {
        this.offset = offset;
        this.color = color;
        if (color != null) {
            Component displayName = ComponentProvider.translatable("text.itemswapper.openPalette." + offset);
            this.icon = new ItemIcon(ItemSwapperSharedMod.instance.getBlockTextureManager()
                    .getBlocksByAverageColor(new UnpackedColor[] { ColorUtil.createTetradPalette(color).get(offset) })
                    .get(0).asItem().getDefaultInstance(), displayName);
        } else {
            icon = new TextureIcon(getResourceLocation("itemswapper", "textures/gui/paint_brush.png"),
                    ComponentProvider.translatable("text.itemswapper.openPalette"));
        }
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        if (color != null) {
            ItemSwapperSharedMod.instance.openPage(
                    new TexturePage(new UnpackedColor[] { ColorUtil.createTetradPalette(color).get(offset) }, color));
            return true;
        } else if (Minecraft.getInstance().player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
            UnpackedColor base = ItemSwapperSharedMod.instance.getBlockTextureManager()
                    .getColor(blockItem.getBlock())[0];
            ItemSwapperSharedMod.instance.openPage(
                    new TexturePage(new UnpackedColor[] { ColorUtil.createTetradPalette(base).get(offset) }, base));
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

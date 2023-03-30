package dev.tr7zw.itemswapper.manager.shortcuts;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackShortcut implements Shortcut {

    private final Icon icon = new TextureIcon(new ResourceLocation("itemswapper", "textures/gui/back.png"), Component.translatable("text.itemswapper.back"));
    private final SwitchItemOverlay overlay;
    private final Component hoverText = Component.translatable("text.itemswapper.back.tooltip");

    public BackShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action) {
        // remove the current page
        overlay.getPageHistory().remove(overlay.getPageHistory().size() - 1);
        overlay.openPage(overlay.getPageHistory().remove(overlay.getPageHistory().size() - 1));
        overlay.selectIcon("back");
        return true;
    }

    @Override
    public boolean isVisible() {
        // one entry is the current page!
        return overlay.getPageHistory().size() > 1;
    }

    @Override
    public Component getHoverText() {
        return hoverText;
    }

    @Override
    public String getSelector() {
        return "back";
    }

}

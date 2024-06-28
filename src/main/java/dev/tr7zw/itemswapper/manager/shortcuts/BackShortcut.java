package dev.tr7zw.itemswapper.manager.shortcuts;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.util.ComponentProvider;

import net.minecraft.network.chat.Component;

public class BackShortcut implements Shortcut {

    private final Icon icon = new TextureIcon(getResourceLocation("itemswapper", "textures/gui/back.png"),
            ComponentProvider.translatable("text.itemswapper.back"));

    private final SwitchItemOverlay overlay;
    private final Component hoverText = ComponentProvider.translatable("text.itemswapper.back.tooltip");

    public BackShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean invoke(SwitchItemOverlay overlay, ActionType action, int xOffset, int yOffset) {
        // remove the current page
        overlay.getLastPages().remove(overlay.getLastPages().size() - 1);
        overlay.openPage(overlay.getLastPages().remove(overlay.getLastPages().size() - 1));
        overlay.selectIcon("back", xOffset, yOffset);
        return true;
    }

    @Override
    public boolean isVisible() {
        // one entry is the current page!
        return overlay.getLastPages().size() > 1;
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

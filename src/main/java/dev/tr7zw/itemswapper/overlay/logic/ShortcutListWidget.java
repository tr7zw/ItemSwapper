package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.LinkIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.TextureIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut.ActionType;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.resources.ResourceLocation;

public class ShortcutListWidget extends ItemGridWidget {

    private final ResourceLocation parentId;
    private final List<Shortcut> list;

    public ShortcutListWidget(ResourceLocation parentId, List<Shortcut> list, int x, int y) {
        super(x, y);
        this.parentId = parentId;
        this.list = list.stream().filter(Shortcut::isVisible).toList();
        WidgetUtil.setupSlots(widgetArea, slots, 1, this.list.size(), false, null);
    }

    @Override
    public List<GuiSlot> getSlots() {
        return slots;
    }

    @Override
    public WidgetArea getWidgetArea() {
        return widgetArea;
    }

    @Override
    protected void renderSlot(RenderContext graphics, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwriteAvailable) {
        Icon icon = list.get(guiSlot.id()).getIcon();
        if (icon instanceof ItemIcon item) {
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player, item.item(), 1,
                    SlotEffect.NONE, 1));
        } else if (icon instanceof LinkIcon item) {
            boolean grayedOut = parentId != null && parentId.equals(item.nextId());
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player, item.item(), 1,
                    grayedOut ? SlotEffect.GRAY : SlotEffect.NONE, 1));
        } else if (icon instanceof TextureIcon texture) {
            graphics.pose().translate(0, 0, RenderContext.LAYERS_ITEM);
            graphics.blit(texture.texture(), x - 1, y, 0, 0, 24, 24, 24, 24);
        }
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable,
            RenderContext graphics) {
        Icon icon = list.get(selected.id()).getIcon();
        if (icon instanceof ItemIcon item) {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(item), item.item(), false, yOffset, maxWidth,
                    graphics);
        } else if (icon instanceof LinkIcon link) {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(link), link.item(), false, yOffset, maxWidth,
                    graphics);
        } else if (icon instanceof TextureIcon texture) {
            RenderHelper.renderSelectedEntryName(texture.name(), false, yOffset, maxWidth, graphics);
        }
    }

    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        Shortcut shortcut = list.get(slot.id());
        if (shortcut.acceptSecondaryClick()) {
            shortcut.invoke(overlay, ActionType.SECONDARY_CLICK, xOffset, yOffset);
        }
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        Shortcut shortcut = list.get(slot.id());
        if (shortcut.acceptPrimaryClick()) {
            return shortcut.invoke(overlay, ActionType.PRIMARY_CLICK, xOffset, yOffset);
        }
        return true;
    }

    @Override
    public void renderSelectedTooltip(SwitchItemOverlay overlay, RenderContext graphics, GuiSlot selected, double x,
            double y) {
        Shortcut shortcut = list.get(selected.id());
        if (shortcut.getHoverText() != null) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, RenderContext.LAYERS_TOOLTIP);
            graphics.renderTooltip(minecraft.font, minecraft.font.split(shortcut.getHoverText(), 170), (int) x,
                    (int) y);
            graphics.pose().popPose();
        }
    }

    @Override
    public String getSelector(GuiSlot slot) {
        return list.get(slot.id()).getSelector();
    }

}

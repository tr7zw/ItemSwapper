package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.WidgetUtil;

public class ShortcutListWidget extends ItemGridWidget {
    
    private final List<Shortcut> list;

    public ShortcutListWidget(List<Shortcut> list, int x, int y) {
        super(x,y);
        this.list = list;
        WidgetUtil.setupSlots(widgetArea, slots, 1, list.size(), false, null);
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
    protected void renderSlot(PoseStack poseStack, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot, boolean overwriteAvailable) {
        ItemEntry item = list.get(guiSlot.id()).getIcon();
        itemRenderList.add(
                () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player,
                        item.getItem().getDefaultInstance(), 1,
                        false, 1));
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, boolean overwrideAvailable) {
        ItemEntry slot = list.get(selected.id()).getIcon();
        RenderHelper.renderSelectedItemName(RenderHelper.getName(slot),
                slot.getItem().getDefaultInstance(), false, yOffset);
    }

    @Override
    public void onClick(SwitchItemOverlay overlay, GuiSlot slot) {
        Shortcut shortcut = list.get(slot.id());
        if(shortcut.acceptClick()) {
            shortcut.invoke();
        }
    }

    @Override
    public void onClose(SwitchItemOverlay overlay, GuiSlot slot) {
        Shortcut shortcut = list.get(slot.id());
        if(shortcut.acceptClose()) {
            shortcut.invoke();
        }
    }

}

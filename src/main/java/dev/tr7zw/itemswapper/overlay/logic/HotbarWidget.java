package dev.tr7zw.itemswapper.overlay.logic;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;

import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.transition.mc.InventoryUtil;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.resources.*;
import net.minecraft.world.item.ItemStack;

public class HotbarWidget extends InventoryAbstractWidget {

    private static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ BACKGROUND_LOCATION = getResourceLocation(
            "itemswapper", "textures/gui/hotbar.png");

    public HotbarWidget(int x, int y) {
        super(x, y);
        WidgetUtil.setupSlots(widgetArea, slots, 9, 1, false, BACKGROUND_LOCATION);
        widgetArea.setBackgroundTextureSizeX(168);
        widgetArea.setBackgroundTextureSizeY(24);
    }

    @Override
    protected List<AvailableSlot> getItem(int id) {
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(minecraft.player.getInventory());
        if (id != -1 && !items.get(id).isEmpty()) {
            return Collections.singletonList(new AvailableSlot(-1, id, items.get(id)));
        }
        return Collections.emptyList();
    }

}

package dev.tr7zw.itemswapper.overlay.logic;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;

import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class InventoryWidget extends InventoryAbstractWidget {

    private static final ResourceLocation BACKGROUND_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/inventory.png");

    public InventoryWidget(int x, int y) {
        super(x, y);
        WidgetUtil.setupSlots(widgetArea, slots, 9, 3, false, BACKGROUND_LOCATION);
        widgetArea.setBackgroundTextureSizeX(168);
        widgetArea.setBackgroundTextureSizeY(60);
    }

    @Override
    protected List<AvailableSlot> getItem(int id) {
        NonNullList<ItemStack> items = minecraft.player.getInventory().items;
        if (id != -1 && !items.get(id + 9).isEmpty()) {
            return Collections.singletonList(new AvailableSlot(-1, id + 9, items.get(id + 9)));
        }
        return Collections.emptyList();
    }

}

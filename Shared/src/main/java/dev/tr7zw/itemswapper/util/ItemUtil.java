package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import net.minecraft.world.item.Item;

public final class ItemUtil {

    private ItemUtil() {
        // private
    }

    public static int inventorySlotToHudSlot(int slot) {
        if (slot < 9) {
            return 36 + slot;
        }
        return slot;
    }

    public static boolean inArray(Item[] items, Item item) {
        for (Item i : items) {
            if (i == item) {
                return true;
            }
        }
        return false;
    }
    
    public static ItemEntry[] toDefault(Item[] items) {
        ItemEntry[] entries = new ItemEntry[items.length];
        for(int i = 0; i < items.length; i++) {
            entries[i] = new ItemEntry(items[i], null);
        }
        return entries;
    }

}

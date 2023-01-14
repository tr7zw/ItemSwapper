package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import java.util.Arrays;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static Item[] itemstackToSingleItem(Item[] items)
    {
        int lastItem = 0;
        for (int x = 0; x < items.length; x++) {
            if (items[x] != Items.AIR) {
                lastItem = x;
            }
        }
        items = Arrays.copyOf(items, lastItem + 1);
        return items;
    }

}

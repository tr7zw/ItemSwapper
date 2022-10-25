package dev.tr7zw.itemswapper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemUtil {

    private static Minecraft minecraft = Minecraft.getInstance();
    private static Set<Item> shulkers = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
            Items.RED_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX);

    private ItemUtil() {
        // private
    }
    
    public static int inventorySlotToHudSlot(int slot) {
        if (slot < 9) {
            return 36 + slot;
        }
        return slot;
    }

    public static List<Slot> findSlotsMatchingItem(Item item, boolean limit) {
        NonNullList<ItemStack> items = minecraft.player.getInventory().items;
        List<Slot> ids = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!(items.get(i)).isEmpty()
                    && items.get(i).getItem() == item) {
                addUnstackableItems(ids, new Slot(-1, i, items.get(i)));
                if(limit) {
                    return ids;
                }
            }
            if (!(items.get(i)).isEmpty()
                    && shulkers.contains(items.get(i).getItem())
                    && ItemSwapperSharedMod.instance.areShulkersEnabled()) {
                List<ItemStack> shulkerItems = ShulkerHelper.getItems(items.get(i));
                if (shulkerItems != null) {
                    for (int x = 0; x < shulkerItems.size(); x++) {
                        if (!(shulkerItems.get(x)).isEmpty()
                                && shulkerItems.get(x).getItem() == item) {
                            addUnstackableItems(ids, new Slot(i, x, shulkerItems.get(x)));
                            if(limit) {
                                return ids;
                            }
                        }
                    }
                }
            }
        }
        return ids;
    }
    
    private static void addUnstackableItems(List<Slot> ids, Slot slot) {
        for(Slot s : ids) {
            if(ItemStack.isSameItemSameTags(s.item, slot.item)) {
                return;
            }
        }
        ids.add(slot);
    }

    public static record Slot(int inventory, int slot, ItemStack item) {

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + inventory;
            result = prime * result + slot;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Slot other = (Slot) obj;
            if (inventory != other.inventory)
                return false;
            if (slot != other.slot)
                return false;
            return true;
        }

    }

}

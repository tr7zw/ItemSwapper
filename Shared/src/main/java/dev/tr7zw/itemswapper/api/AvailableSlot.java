package dev.tr7zw.itemswapper.api;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.world.item.ItemStack;

/**
 * @param inventory The inventory containing the item. -1 is the player
 *                  inventory, positive numbers point to a container inside the
 *                  player inventory(shulkers). Other unique negative ids can be
 *                  used by other mods to point to other inventories like the
 *                  enderchest.
 * @param slot      The slot id inside the inventory
 * @param item      The item used for rendering/reference
 * @parm amount Only used to calculate the amount of available total items
 */
public record AvailableSlot(int inventory, int slot, ItemStack item, AtomicInteger amount) {

    /**
     * @param inventory The inventory containing the item. -1 is the player
     *                  inventory, positive numbers point to a container inside the
     *                  player inventory(shulkers). Other unique negative ids can be
     *                  used by other mods to point to other inventories like the
     *                  enderchest.
     * @param slot      The slot id inside the inventory
     * @param item      The item used for rendering/reference
     */
    public AvailableSlot(int inventory, int slot, ItemStack item) {
        this(inventory, slot, item, new AtomicInteger(item.getCount()));
    }

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
        AvailableSlot other = (AvailableSlot) obj;
        if (inventory != other.inventory)
            return false;
        if (slot != other.slot)
            return false;
        return true;
    }

}

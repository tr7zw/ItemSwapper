package dev.tr7zw.itemswapper.api.client;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import dev.tr7zw.itemswapper.api.AvailableSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Process containers inside the players inventory, returning a list of
 * {@link AvailableSlot}
 * 
 * @author tr7zw
 *
 */
public interface ContainerProvider {

    /**
     * @return A list of all Items this provider wants to process
     */
    Set<Item> getItemHandlers();

    /**
     * @param itemStack The itemstack to process
     * @param item      The target item to find
     * @param limit     if true, bestcase only a single slot should be returned, the
     *                  rest will be ignored
     * @return A list of {@link AvailableSlot}s that can be swapped to right now.
     *         Return an {@link Collections#emptyList() emptyList} when nothing is
     *         found/the
     *         itemstack can't be used.
     */
    List<AvailableSlot> processItemStack(ItemStack itemStack, Item item, boolean limit, int slotId);

    NonNullList<AvailableSlot> getItemStacks(ItemStack itemStack, int slotId);

}

package dev.tr7zw.itemswapper.api.client;

import java.util.List;

import dev.tr7zw.itemswapper.api.AvailableSlot;
import net.minecraft.world.item.Item;

/**
 * Provide {@link AvailableSlot}s from outside the players inventory, that are
 * not containers inside the inventory.
 * 
 * @author tr7zw
 *
 */
public interface ItemProvider {

    /**
     * @param item  The target item
     * @param limit if true, bestcase only a single slot should be returned, the
     *              rest will be ignored
     * @return A list of {@link AvailableSlot}s that can be swapped to right now.
     *         Return an {@link Collections#emptyList() emptyList} when nothing is
     *         found
     */
    List<AvailableSlot> findSlotsMatchingItem(Item item, boolean limit);

}

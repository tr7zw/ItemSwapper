package dev.tr7zw.itemswapper.api.client;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface NameProvider {

    /**
     * @return A list of all Items this provider wants to process
     */
    Set<Item> getItemHandlers();
    
    /**
     * Provide a formatted display name for the provided item.
     * 
     * @param item
     * @return
     */
    Component getDisplayName(ItemStack item);
    
}

package dev.tr7zw.itemswapper.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface NameProvider {

    /**
     * Check if this NameProvider wants to provide the display name for this item
     * 
     * @param item
     * @return
     */
    boolean isProvider(ItemStack item);

    /**
     * Provide a formatted display name for the provided item.
     * 
     * @param item
     * @return
     */
    Component getDisplayName(ItemStack item);

}

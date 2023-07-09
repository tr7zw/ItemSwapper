package dev.tr7zw.itemswapper.accessor;

import java.util.Set;

import net.minecraft.world.item.Item;

/**
 * Grabbing the internal Item map, should add support to modded variants
 * 
 * @author tr7zw
 *
 */
public interface ItemVariantAccess {

    public Set<Item> getAllItemVariants();

}

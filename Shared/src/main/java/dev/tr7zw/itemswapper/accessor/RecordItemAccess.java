package dev.tr7zw.itemswapper.accessor;

import java.util.Set;

import net.minecraft.world.item.Item;

/**
 * Grabbing the internal Record map should add support to modded music discs
 * 
 * @author tr7zw
 *
 */
public interface RecordItemAccess {

    public Set<Item> getAllRecords();

}

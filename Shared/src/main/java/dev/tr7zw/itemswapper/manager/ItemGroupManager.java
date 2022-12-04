package dev.tr7zw.itemswapper.manager;

import java.util.HashMap;
import java.util.Map;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemGroupManager {

    private Map<Item, Item[]> mapping = new HashMap<>();
    private Map<Item, Item[]> secondaryMapping = new HashMap<>();
    private Map<Item, Item[]> listMapping = new HashMap<>();
    
    public void reset() {
        mapping.clear();
        secondaryMapping.clear();
        listMapping.clear();
    }
    
    public void registerCollections(Item[]... collection) {
        for(Item[] col : collection) {
            if(col == null || col.length == 0) {
                ItemSwapperSharedMod.LOGGER.warn("Tried to register invalid empty collection!");
                return;
            }
        }
        for(int i = 0; i < collection.length; i++) {
            Item[] col = collection[i];
            Item[] target = collection[i+1 == collection.length ? 0 : i+1];
            for(Item item : col) {
                if(item != Items.AIR) {
                    mapping.put(item, col);
                    secondaryMapping.put(item, target);
                }
            }
        }
    }
    
    public void registerCollection(Item[] items) {
        if(items.length == 0) {
            ItemSwapperSharedMod.LOGGER.warn("Tried to register invalid collection!");
            return;
        }
        for(Item i : items) {
            if(i != Items.AIR) {
                mapping.put(i, items);
            }
        }
    }
    
    public void registerSecondaryCollection(Item[] items) {
        if(items.length == 0) {
            ItemSwapperSharedMod.LOGGER.warn("Tried to register invalid collection!");
            return;
        }
        for(Item i : items) {
            if(i != Items.AIR) {
                secondaryMapping.put(i, items);
            }
        }
    }
    
    public void registerListCollection(Item[] items) {
        for(Item i : items) {
            if(i != Items.AIR) {
                listMapping.put(i, items);
            }
        }
    }
    
    public Item[] getSelection(Item item) {
        if(mapping.containsKey(item)) {
            return mapping.get(item);
        }
        return null;
    }
    
    public Item[] getSecondarySelection(Item item) {
        if(secondaryMapping.containsKey(item)) {
            return secondaryMapping.get(item);
        }
        return null;
    }
    
    public Item[] getList(Item item) {
        if(listMapping.containsKey(item)) {
            return listMapping.get(item);
        }
        return null;
    }
    
    public Item[] getOpenList(Item item) {
        Item[] entries = getSelection(item);
        if (entries != null) {
            Item[] secondary = getSecondarySelection(item);
            if (secondary != null && !ItemUtil.inArray(entries, item)) {
                return secondary;
            }
        }
        return entries;
    }
    
    public Item[] nextList(Item item, Item[] currentList) {
        Item[] entries = getSelection(item);
        Item[] secondary = getSecondarySelection(item);
        if(entries != null && currentList != entries) {
            return entries;
        }
        if(secondary != null && currentList != secondary) {
            return secondary;
        }
        return getOpenList(item);
    }

    /**
     * Checks if resource pack is selected.
     * @return True if item groups could be loaded, false if at least one Hashmap is empty.
     */
    public boolean isResourcepackSelected() {
        return !mapping.isEmpty() && !secondaryMapping.isEmpty() && !listMapping.isEmpty();
    }

}

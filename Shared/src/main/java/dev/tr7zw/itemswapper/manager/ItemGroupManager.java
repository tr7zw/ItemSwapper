package dev.tr7zw.itemswapper.manager;

import java.util.HashMap;
import java.util.Map;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
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
    
    public void registerDualCollection(Item[] primary, Item[] secondary) {
        if(primary.length == 0 || secondary.length == 0 ) {
            ItemSwapperSharedMod.LOGGER.warn("Tried to register invalid collection!");
            return;
        }
        for(Item i : primary) {
            if(i != Items.AIR) {
                mapping.put(i, primary);
                secondaryMapping.put(i, secondary);
            }
        }
        for(Item i : secondary) {
            if(i != Items.AIR) {
                mapping.put(i, primary);
                secondaryMapping.put(i, secondary);
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
    
}

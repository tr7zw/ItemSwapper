package dev.tr7zw.xisumatweeks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemGroupManager {

    private final Item[] fallback = new Item[] {Items.AIR,Items.AIR,Items.AIR,Items.AIR,Items.AIR,Items.AIR,Items.AIR,Items.AIR};
    private Map<Item, Item[]> mapping = new HashMap<>();
    private Map<Item, Item[]> secondaryMapping = new HashMap<>();
    
    public ItemGroupManager() {
        registerCollection(new Item[] {Items.BIRCH_PLANKS, Items.BIRCH_SLAB, Items.BIRCH_STAIRS, Items.BIRCH_TRAPDOOR, Items.BIRCH_BUTTON, Items.BIRCH_FENCE, Items.BIRCH_PRESSURE_PLATE, Items.BIRCH_FENCE_GATE});
        registerSecondaryCollection(new Item[] {Items.OAK_PLANKS, Items.BIRCH_PLANKS, Items.SPRUCE_PLANKS, Items.JUNGLE_PLANKS, Items.ACACIA_PLANKS, Items.DARK_OAK_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS});
    }
    
    public void registerCollection(Item[] items) {
        if(items.length != 8) {
            XisumatweeksSharedMod.LOGGER.warn("Tried to register invalid collection!");
            return;
        }
        for(Item i : items) {
            mapping.put(i, items);
        }
    }
    
    public void registerSecondaryCollection(Item[] items) {
        if(items.length != 8) {
            XisumatweeksSharedMod.LOGGER.warn("Tried to register invalid collection!");
            return;
        }
        for(Item i : items) {
            secondaryMapping.put(i, items);
        }
    }
    
    public Item[] getSelection(Item item) {
        if(mapping.containsKey(item)) {
            return mapping.get(item);
        }
        return fallback;
    }
    
    public Item[] getSecondarySelection(Item item) {
        if(secondaryMapping.containsKey(item)) {
            return secondaryMapping.get(item);
        }
        return fallback;
    }
    
}

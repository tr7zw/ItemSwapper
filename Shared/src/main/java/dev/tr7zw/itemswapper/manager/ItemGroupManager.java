package dev.tr7zw.itemswapper.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemGroupManager {

    private Map<ResourceLocation, ItemGroup> groupMapping = new HashMap<>();
    private Map<Item, List<ItemGroup>> paletteMapping = new HashMap<>();
    private Map<Item, Item[]> listMapping = new HashMap<>();

    public void reset() {
        listMapping.clear();
        groupMapping.clear();
        paletteMapping.clear();
    }

    public void registerItemGroup(ItemGroup group) {
        if(group.getId() == null) {
            ItemSwapperSharedMod.LOGGER.warn("Tried to register an ItemGroup without any id!");
            return;
        }
        groupMapping.put(group.getId(), group);
        if(group.autoLinkDisabled()) {
            // Dont add to the paletteMappings
            return;
        }
        for (ItemEntry item : group.getItems()) {
            addOpener(group, item);
        }
        for (ItemEntry item : group.getOpenOnlyItems()) {
            addOpener(group, item);
        }
    }

    private void addOpener(ItemGroup group, ItemEntry item) {
        List<ItemGroup> list = paletteMapping.computeIfAbsent(item.getItem(), k -> new ArrayList<>());
        if (list.contains(group)) {
            return;
        }
        list.add(group);
        list.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }

    public ItemGroup getNextPage(ItemGroup current, ItemEntry clicked) {
        if (clicked.getLink() != null) {
            ItemGroup group = groupMapping.get(clicked.getLink());
            if (group != null) {
                return group;
            }
        }
        if (current.getForcedLink() != null) {
            ItemGroup group = groupMapping.get(current.getForcedLink());
            if (group != null) {
                return group;
            }
        }
        List<ItemGroup> list = paletteMapping.get(clicked.getItem());
        if (list != null && !list.isEmpty()) {
            int cur = list.indexOf(current) + 1;
            if (cur == 0) { // getting here from somewhere else
                return list.get(0);
            }
            // bounds checking, looping back to 0 in that case
            if (cur >= list.size()) {
                cur = 0;
            }
            return list.get(cur);
        }
        if (current.getFallbackLink() != null) {
            ItemGroup group = groupMapping.get(current.getFallbackLink());
            if (group != null) {
                return group;
            }
        }
        return null;
    }

    public ItemGroup getItemPage(Item item) {
        List<ItemGroup> list = paletteMapping.get(item);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    public void registerListCollection(Item[] items) {
        for (Item i : items) {
            if (i != Items.AIR) {
                listMapping.put(i, items);
            }
        }
    }

    public Item[] getList(Item item) {
        if (listMapping.containsKey(item)) {
            return listMapping.get(item);
        }
        return null;
    }

    /**
     * Checks if resource pack is selected.
     * 
     * @return True if item groups could be loaded, false if at least one Hashmap is
     *         empty.
     */
    public boolean isResourcepackSelected() {
        return !paletteMapping.isEmpty() && !listMapping.isEmpty();
    }

}

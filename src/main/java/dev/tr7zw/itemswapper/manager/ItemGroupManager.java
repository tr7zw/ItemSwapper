package dev.tr7zw.itemswapper.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dev.tr7zw.itemswapper.ItemSwapperBase;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemGroupManager {

    private Map</*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/, ItemGroup> groupMapping = new HashMap<>();
    private Map<Item, List<ItemGroup>> paletteMapping = new HashMap<>();
    private Map</*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/, ItemList> listKeyMapping = new HashMap<>();
    private Map<Item, ItemList> listMapping = new HashMap<>();
    private Map<Item, ItemGroup> lastPicked = new HashMap<>();

    public void reset() {
        listKeyMapping.clear();
        listMapping.clear();
        groupMapping.clear();
        paletteMapping.clear();
    }

    public void registerItemGroup(ItemGroup group) {
        if (group.getId() == null) {
            ItemSwapperBase.LOGGER.warn("Tried to register an ItemGroup without any id!");
            return;
        }
        groupMapping.put(group.getId(), group);
        // Add these before the autoLinkDisabled check, since they would do nothing
        // otherwise in that case
        for (Item item : group.getOpenOnlyItems()) {
            addOpener(group, item);
        }
        if (group.autoLinkDisabled()) {
            // Dont add to the paletteMappings
            return;
        }
        for (ItemEntry item : group.getItems()) {
            if (!group.getIgnoreItems().contains(item.getItem())) {
                addOpener(group, item.getItem());
            }
        }
    }

    private void addOpener(ItemGroup group, Item item) {
        List<ItemGroup> list = paletteMapping.computeIfAbsent(item, k -> new ArrayList<>());
        if (list.contains(group)) {
            return;
        }
        list.add(group);
        list.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }

    /**
     * @param current current ItemGroup, null if not in an ItemGroup view
     * @param clicked
     * @return
     */
    public Page getNextPage(ItemGroup current, ItemEntry clicked, int slot) {
        if (clicked.getLink() != null) {
            ItemGroup group = groupMapping.get(clicked.getLink());
            if (group != null) {
                return new ItemGroupPage(group);
            }
            ItemList list = listKeyMapping.get(clicked.getLink());
            if (list != null) {
                return new ListPage(list);
            }
        }
        if (current != null && current.getForcedLink() != null) {
            ItemGroup group = groupMapping.get(current.getForcedLink());
            if (group != null) {
                return new ItemGroupPage(group);
            }
        }
        // check if it's a valid container that can be opened
        if (current == null && slot != -1 && !ConfigManager.getInstance().getConfig().disableShulkers) {
            ContainerProvider provider = ItemSwapperSharedMod.instance.getClientProviderManager()
                    .getContainerProvider(clicked.getItem());
            if (provider != null) {
                return new ContainerPage(slot);
            }
        }
        // check for links
        List<ItemGroup> list = paletteMapping.get(clicked.getItem());
        if (list != null && !list.isEmpty()) {
            int cur = 0;
            if (current != null) {
                cur = list.indexOf(current) + 1;
                if (cur == 0) { // getting here from somewhere else
                    return new ItemGroupPage(list.get(0));
                }
            }
            // bounds checking, looping back to 0 in that case
            if (cur >= list.size()) {
                cur = 0;
            }
            if (current == null || list.get(cur) != current) {
                // only return the next one, if it's different to the current one, otherwise use
                // the fallback
                return new ItemGroupPage(list.get(cur));
            }
        }
        if (listMapping.containsKey(clicked.getItem())) {
            return new ListPage(listMapping.get(clicked.getItem()));
        }
        if (current != null && current.getFallbackLink() != null) {
            ItemGroup group = groupMapping.get(current.getFallbackLink());
            if (group != null) {
                return new ItemGroupPage(group);
            }
        }
        return NO_PAGE;
    }

    public void setLastPickedItem(Item item, ItemGroup group) {
        this.lastPicked.put(item, group);
    }

    public ItemGroup getLastPickedItemGroup(Item item) {
        return this.lastPicked.get(item);
    }

    public ItemGroup getItemPage(Item item) {
        List<ItemGroup> list = paletteMapping.get(item);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    public void registerListCollection(ItemList items) {
        if (!items.isDisableAutoLink()) {
            for (Item i : items.getItems()) {
                if (i != Items.AIR && !items.getIgnoreItems().contains(i)) {
                    listMapping.put(i, items);
                }
            }
            for (Item i : items.getOpenOnlyItems()) {
                listMapping.put(i, items);
            }
        }
        listKeyMapping.put(items.getId(), items);
    }

    public ItemList getList(Item item) {
        if (listMapping.containsKey(item)) {
            return listMapping.get(item);
        }
        return null;
    }

    public Page getPage(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ location) {
        if (groupMapping.containsKey(location)) {
            return new ItemGroupPage(groupMapping.get(location));
        } else if (listKeyMapping.containsKey(location)) {
            return new ListPage(listKeyMapping.get(location));
        }
        return NO_PAGE;
    }

    /**
     * Write to console a list of all unmapped items
     */
    public void dumpUnmappedItems() {
        ItemSwapperBase.LOGGER.info("All unmapped Items/Blocks:");
        for (Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (!(paletteMapping.containsKey(entry.getValue()) || listMapping.containsKey(entry.getValue()))) {
                ItemSwapperBase.LOGGER.info("Unmapped: " + entry.getKey());
            }
        }
        groupMapping.values().stream()
                .sorted((a, b) -> Integer.compare(((ItemGroup) b).getItems().length, ((ItemGroup) a).getItems().length))
                .limit(5).forEach(i -> System.out.println("Group: " + i.getId() + " Size: " + i.getItems().length));
        for (ItemGroup group : groupMapping.values()) {
            if (group.getDisplayName().getString().equals(
                    group.getDisplayName().toString().replace("translation{key='", "").replace("', args=[]}", ""))) {
                System.out.println("Broken name in " + group.getId() + ": " + group.getDisplayName());
            }
        }
    }

    /**
     * Checks if resource pack is selected.
     * 
     * @return True if item groups could be loaded, false if at least one Hashmap is
     *         empty.
     */
    public boolean isResourcepackSelected() {
        return !paletteMapping.isEmpty() || !listMapping.isEmpty();
    }

    public sealed interface Page {
    }

    public record ItemGroupPage(ItemGroup group) implements Page {
    }

    public record ListPage(ItemList items) implements Page {
    }

    public record NoPage() implements Page {
    }

    public record TexturePage(UnpackedColor[] color, UnpackedColor sideBase) implements Page {
    }

    public record InventoryPage() implements Page {
    }

    public record ContainerPage(int containerSlotId) implements Page {
    }

    private static final NoPage NO_PAGE = new NoPage();

}

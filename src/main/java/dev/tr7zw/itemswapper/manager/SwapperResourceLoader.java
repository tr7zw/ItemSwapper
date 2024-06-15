package dev.tr7zw.itemswapper.manager;

//spotless:off
//#if MC >= 12100
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.tr7zw.itemswapper.ItemSwapperBase;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup.Builder;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroupModifier;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemListModifier;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LinkShortcut;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SwapperResourceLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public SwapperResourceLoader() {
        super(GSON, "itemgroups");

    }

    private List<ItemGroup.Builder> itemGroups = new ArrayList<>();
    private List<ItemList.Builder> itemLists = new ArrayList<>();
    private List<ItemGroupModifier> itemGroupModifiers = new ArrayList<>();
    private List<ItemListModifier> itemListModifiers = new ArrayList<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
                         ProfilerFiller profilerFiller) {
        itemGroups.clear();
        itemGroupModifiers.clear();
        itemListModifiers.clear();
        itemLists.clear();
        ItemSwapperBase.LOGGER.info("Processing item groups: " + map.keySet());
        ItemSwapperSharedMod.instance.getItemGroupManager().reset();
        for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            processEntry(entry);
        }
        applyModifications();
        registerItemGroups();
        itemGroups.clear();
        itemGroupModifiers.clear();
        itemListModifiers.clear();
        itemLists.clear();
    }

    private void processEntry(Entry<ResourceLocation, JsonElement> entry) {
        try {
            if (!entry.getKey().getNamespace().equals("itemswapper"))
                return;
            if (entry.getKey().getPath().startsWith("wheel_combined/")) {
                processCombined(entry.getKey(), entry.getValue());
                return;
            }
            if (entry.getKey().getPath().startsWith("v2/")) {
                processV2(entry.getKey(), entry.getValue());
                return;
            }
            Item[] items = getItemArray(entry.getKey(), entry.getValue(), entry.getKey().getPath().startsWith("wheel"));
            if (items != null) {
                Builder group = ItemGroup.builder().withId(entry.getKey()).withItems(ItemUtil.toDefault(items));
                if (entry.getKey().getPath().startsWith("wheel_primary/")) {
                    itemGroups.add(group.withPriority(100));
                }
                if (entry.getKey().getPath().startsWith("wheel_secondary/")) {
                    itemGroups.add(group.withPriority(200));
                }
                if (entry.getKey().getPath().startsWith("list/")) {
                    itemLists.add(ItemList.builder().withId(entry.getKey()).withItems(items));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerItemGroups() {
        for (int i = 0; i < itemGroups.size(); i++) {
            ItemSwapperSharedMod.instance.getItemGroupManager()
                    .registerItemGroup(itemGroups.get(i).withItems(filterAir(itemGroups.get(i).getItems())).build());
        }
        for (int i = 0; i < itemLists.size(); i++) {
            ItemSwapperSharedMod.instance.getItemGroupManager().registerListCollection(itemLists.get(i).build());
        }
    }

    private ItemEntry[] filterAir(ItemEntry[] items) {
        List<ItemEntry> filteredEntries = new ArrayList<ItemEntry>();
        for (ItemEntry entry : items) {
            if (entry.getItem() != Items.AIR) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries.toArray(new ItemEntry[0]);
    }

    private void applyModifications() {
        for (int i = 0; i < itemGroupModifiers.size(); i++) {
            ItemGroupModifier modifier = itemGroupModifiers.get(i);
            for (ItemGroup.Builder group : itemGroups) {
                if (modifier.getTarget().equals(group.getId())) {
                    List<ItemEntry> entries = new ArrayList<>(Arrays.asList(group.getItems()));
                    if (modifier.getRemoveItems() != null) {
                        for (ItemEntry remove : modifier.getRemoveItems()) {
                            entries.removeIf(entry -> (entry.getItem().equals(remove.getItem())));
                        }
                    }
                    if (modifier.getAddItems() != null) {
                        entries.addAll(Arrays.asList(modifier.getAddItems()));
                    }
                    group.withItems(entries.toArray(new ItemEntry[0]));
                    break;
                }
            }
        }
        for (int i = 0; i < itemListModifiers.size(); i++) {
            ItemListModifier modifier = itemListModifiers.get(i);
            for (ItemList.Builder list : itemLists) {
                if (modifier.getTarget().equals(list.getId())) {
                    List<Item> entries = new ArrayList<>(Arrays.asList(list.getItems()));
                    if (modifier.getRemoveItems() != null) {
                        for (Item remove : modifier.getRemoveItems()) {
                            entries.removeIf(entry -> (entry.equals(remove)));
                        }
                    }
                    if (modifier.getAddItems() != null) {
                        entries.addAll(Arrays.asList(modifier.getAddItems()));
                    }
                    list.withItems(entries.toArray(new Item[0]));
                    break;
                }
            }
        }
    }

    private void processV2(ResourceLocation jsonLocation, JsonElement json) {
        if (!json.isJsonObject()) {
            ItemSwapperBase.LOGGER.warn("Invalid data in " + jsonLocation);
            return;
        }
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        if (type.equals("palette")) {
            processPalette(jsonLocation, obj);
            return;
        }
        if (type.equals("paletteModification")) {
            processPaletteModification(jsonLocation, obj);
            return;
        }
        if (type.equals("listModification")) {
            processListModification(jsonLocation, obj);
            return;
        }
        if (type.equals("list")) {
            processList(jsonLocation, obj);
            return;
        }
    }

    private void processList(ResourceLocation jsonLocation, JsonObject json) {
        dev.tr7zw.itemswapper.manager.itemgroups.ItemList.Builder group = ItemList.builder().withId(jsonLocation);
        if (json.has("disableAutoLink") && json.get("disableAutoLink").isJsonPrimitive()) {
            group.withDisableAutoLink(json.get("disableAutoLink").getAsBoolean());
        }
        if (json.has("displayName") && json.get("displayName").isJsonPrimitive()) {
            group.withDisplayName(ComponentProvider.translatable(json.get("displayName").getAsString()));
        }
        if (json.has("link") && json.get("link").isJsonPrimitive()) {
            try {
                group.withLink(ResourceLocation.tryParse(json.getAsJsonPrimitive("link").getAsString()));
            } catch (Exception ex) {
                ItemSwapperBase.LOGGER.warn("Invalid link in " + jsonLocation);
            }
        }
        group.withItems(getItemArray(jsonLocation, json.get("items"), false));
        Item[] openOnly = getItemArray(jsonLocation, json.get("openOnlyItems"), false);
        if (openOnly != null && openOnly.length > 0) {
            group.withOpenOnlyItems(new HashSet<>(Arrays.asList(openOnly)));
        }
        Item[] ignoreItems = getItemArray(jsonLocation, json.get("ignoreItems"), false);
        if (ignoreItems != null && ignoreItems.length > 0) {
            group.withIgnoreItems(new HashSet<>(Arrays.asList(ignoreItems)));
        }
        if (json.has("icon") && json.get("icon").isJsonPrimitive()) {
            group.withIcon(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(json.get("icon").getAsString())));
        }
        itemLists.add(group);
    }

    private void processPalette(ResourceLocation jsonLocation, JsonObject json) {
        Builder group = ItemGroup.builder().withId(jsonLocation);
        if (json.has("priority") && json.get("priority").isJsonPrimitive()) {
            group.withPriority(json.getAsJsonPrimitive("priority").getAsInt());
        } else {
            group.withPriority(100);
        }
        if (json.has("fallbackLink") && json.get("fallbackLink").isJsonPrimitive()) {
            try {
                group.withFallbackLink(ResourceLocation.tryParse(json.getAsJsonPrimitive("fallbackLink").getAsString()));
            } catch (Exception ex) {
                ItemSwapperBase.LOGGER.warn("Invalid fallbackLink in " + jsonLocation);
            }
        }
        if (json.has("forceLink") && json.get("forceLink").isJsonPrimitive()) {
            try {
                group.withForcedLink(ResourceLocation.tryParse(json.getAsJsonPrimitive("forceLink").getAsString()));
            } catch (Exception ex) {
                ItemSwapperBase.LOGGER.warn("Invalid forceLink in " + jsonLocation);
            }
        }
        if (json.has("disableAutoLink") && json.get("disableAutoLink").isJsonPrimitive()) {
            group.withDisableAutoLink(json.get("disableAutoLink").getAsBoolean());
        }
        if (json.has("displayName") && json.get("displayName").isJsonPrimitive()) {
            group.withDisplayName(ComponentProvider.translatable(json.get("displayName").getAsString()));
        }
        group.withItems(processItems(jsonLocation, json.get("items")));
        Item[] openOnly = getItemArray(jsonLocation, json.get("openOnlyItems"), false);
        if (openOnly != null && openOnly.length > 0) {
            group.withOpenOnlyItems(new HashSet<>(Arrays.asList(openOnly)));
        }
        Item[] ignoreItems = getItemArray(jsonLocation, json.get("ignoreItems"), false);
        if (ignoreItems != null && ignoreItems.length > 0) {
            group.withIgnoreItems(new HashSet<>(Arrays.asList(ignoreItems)));
        }
        group.withShortcuts(processShortcuts(jsonLocation, json.get("shortcuts")));
        if (json.has("icon") && json.get("icon").isJsonPrimitive()) {
            group.withIcon(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(json.get("icon").getAsString())));
        }
        itemGroups.add(group);
    }

    private void processListModification(ResourceLocation jsonLocation, JsonObject json) {
        dev.tr7zw.itemswapper.manager.itemgroups.ItemListModifier.Builder changes = ItemListModifier.builder();
        if (json.has("target") && json.get("target").isJsonPrimitive()) {
            try {
                changes.withTarget(ResourceLocation.tryParse(json.getAsJsonPrimitive("target").getAsString()));
            } catch (Exception ex) {
                ItemSwapperBase.LOGGER.warn("Invalid target in " + jsonLocation);
                return;
            }
        }
        changes.withAddItems(getItemArray(jsonLocation, json.get("addItems"), false));
        changes.withRemoveItems(getItemArray(jsonLocation, json.get("removeItems"), false));
        itemListModifiers.add(changes.build());
    }

    private void processPaletteModification(ResourceLocation jsonLocation, JsonObject json) {
        dev.tr7zw.itemswapper.manager.itemgroups.ItemGroupModifier.Builder changes = ItemGroupModifier.builder();
        if (json.has("target") && json.get("target").isJsonPrimitive()) {
            try {
                changes.withTarget(ResourceLocation.tryParse(json.getAsJsonPrimitive("target").getAsString()));
            } catch (Exception ex) {
                ItemSwapperBase.LOGGER.warn("Invalid target in " + jsonLocation);
                return;
            }
        }
        changes.withAddItems(processItems(jsonLocation, json.get("addItems")));
        changes.withRemoveItems(processItems(jsonLocation, json.get("removeItems")));
        itemGroupModifiers.add(changes.build());
    }

    private List<Shortcut> processShortcuts(ResourceLocation jsonLocation, JsonElement object) {
        if (object == null || !object.isJsonArray()) {
            return Collections.emptyList();
        }
        List<Shortcut> shortcuts = new ArrayList<>();
        object.getAsJsonArray().forEach(el -> {
            if (!el.isJsonObject()) {
                return;
            }
            JsonObject entry = el.getAsJsonObject();
            if (entry.has("type") && entry.get("type").isJsonPrimitive()
                    && "link".equals(entry.get("type").getAsString())) {
                String displayname = entry.has("displayName") && entry.get("displayName").isJsonPrimitive()
                        ? entry.get("displayName").getAsString()
                        : null;
                Item icon = null;
                if (entry.has("icon") && entry.get("icon").isJsonPrimitive()) {
                    icon = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(entry.get("icon").getAsString()));
                }
                try {
                    shortcuts.add(
                            new LinkShortcut(ResourceLocation.tryParse(entry.getAsJsonPrimitive("target").getAsString()),
                                    displayname != null ? ComponentProvider.translatable(displayname) : null, icon));
                } catch (Exception ex) {
                    ItemSwapperBase.LOGGER.warn("Invalid link target shortcut in " + jsonLocation);
                }
            }
        });
        return shortcuts;
    }

    private ItemEntry[] processItems(ResourceLocation jsonLocation, JsonElement object) {
        if (object == null || !object.isJsonArray()) {
            return null;
        }
        List<ItemEntry> itemList = new ArrayList<>();
        object.getAsJsonArray().forEach(el -> {
            if (el.isJsonPrimitive()) {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(el.getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                if (item == Items.AIR) {
                    ItemSwapperBase.LOGGER.info("Unable to find " + resourceLocation + ", ignoring.");
                }
                ItemEntry entry = new ItemEntry(item, null);
                if (!itemList.contains(entry)) {
                    itemList.add(entry);
                }
            }
            if (el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                ResourceLocation resourceLocation = ResourceLocation.tryParse(obj.get("id").getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                if (item == Items.AIR) {
                    ItemSwapperBase.LOGGER.info("Unable to find " + resourceLocation + ", ignoring.");
                }
                ResourceLocation link = null;
                if (obj.has("link") && obj.get("link").isJsonPrimitive()) {
                    try {
                        link = ResourceLocation.tryParse((obj.get("link").getAsString()));
                    } catch (Exception ex) {
                        ItemSwapperBase.LOGGER.warn("Invalid item link in " + jsonLocation);
                    }
                }
                String displayName = null;
                if (obj.has("name") && obj.get("name").isJsonPrimitive()) {
                    displayName = obj.getAsJsonPrimitive("name").getAsString();
                }
                boolean actAsLink = false;
                if (obj.has("actAsLink") && obj.get("actAsLink").isJsonPrimitive()) {
                    actAsLink = obj.getAsJsonPrimitive("actAsLink").getAsBoolean();
                }
                ItemEntry entry = new ItemEntry(item, link,
                        displayName != null ? ComponentProvider.translatable(displayName) : null, actAsLink);
                if (!itemList.contains(entry)) {
                    itemList.add(entry);
                }
            }
        });
        if (!itemList.isEmpty()) {
            return itemList.toArray(new ItemEntry[0]);
        }
        return null;
    }

    private void processCombined(ResourceLocation jsonLocation, JsonElement json) {
        if (json == null || !json.isJsonArray()) {
            return;
        }
        JsonArray ar = json.getAsJsonArray();
        List<Item[]> lists = new ArrayList<>();
        for (int i = 0; i < ar.size(); i++) {
            Item[] list = getItemArray(jsonLocation, ar.get(i), true);
            if (list != null && list.length > 0) {
                lists.add(list);
            }
        }
        if (lists.isEmpty()) {
            return;
        }
        for (int i = 0; i < lists.size(); i++) {
            ResourceLocation ownId = ResourceLocation.fromNamespaceAndPath(jsonLocation.getNamespace(), jsonLocation.getPath() + i);
            int next = i + 1 == lists.size() ? 0 : i + 1;
            ResourceLocation nextId = ResourceLocation.fromNamespaceAndPath(jsonLocation.getNamespace(), jsonLocation.getPath() + next);
            itemGroups.add(
                    ItemGroup.builder().withId(ownId).withForcedLink(nextId).withItems(ItemUtil.toDefault(lists.get(i)))
                            .withShortcuts(Arrays.asList(new LinkShortcut(nextId))));
        }
    }

    private Item[] getItemArray(ResourceLocation jsonLocation, JsonElement json, boolean pallet) {
        if (json == null || !json.isJsonArray()) {
            return null;
        }
        List<Item> itemList = new ArrayList<>();
        json.getAsJsonArray().forEach(el -> {
            if (el.isJsonPrimitive()) {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(el.getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                if (item.equals(Items.AIR)) {
                    ItemSwapperBase.LOGGER.warn("Unknown item: " + el.getAsString() + " in " + jsonLocation);
                    if (pallet) {
                        itemList.add(Items.AIR);
                    }
                    return;
                }
                if (!itemList.contains(item)) {
                    itemList.add(item);
                }
            }
        });
        if (!itemList.isEmpty()) {
            return itemList.toArray(new Item[0]);
        }
        return null;
    }

}
//#else
//$$ import java.util.ArrayList;
//$$ import java.util.Arrays;
//$$ import java.util.Collections;
//$$ import java.util.HashSet;
//$$ import java.util.List;
//$$ import java.util.Map;
//$$ import java.util.Map.Entry;
//$$
//$$ import com.google.gson.Gson;
//$$ import com.google.gson.GsonBuilder;
//$$ import com.google.gson.JsonArray;
//$$ import com.google.gson.JsonElement;
//$$ import com.google.gson.JsonObject;
//$$
//$$ import dev.tr7zw.itemswapper.ItemSwapperBase;
//$$ import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup.Builder;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroupModifier;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.ItemListModifier;
//$$ import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
//$$ import dev.tr7zw.itemswapper.manager.shortcuts.LinkShortcut;
//$$ import dev.tr7zw.itemswapper.util.ItemUtil;
//$$ import dev.tr7zw.util.ComponentProvider;
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import net.minecraft.server.packs.resources.ResourceManager;
//$$ import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
//$$ import net.minecraft.util.profiling.ProfilerFiller;
//$$ import net.minecraft.world.item.Item;
//$$ import net.minecraft.world.item.Items;
//$$
//$$ public class SwapperResourceLoader extends SimpleJsonResourceReloadListener {
//$$     private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
//$$
//$$     public SwapperResourceLoader() {
//$$         super(GSON, "itemgroups");
//$$
//$$     }
//$$
//$$     private List<ItemGroup.Builder> itemGroups = new ArrayList<>();
//$$     private List<ItemList.Builder> itemLists = new ArrayList<>();
//$$     private List<ItemGroupModifier> itemGroupModifiers = new ArrayList<>();
//$$     private List<ItemListModifier> itemListModifiers = new ArrayList<>();
//$$
//$$     @Override
//$$     protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
//$$             ProfilerFiller profilerFiller) {
//$$         itemGroups.clear();
//$$         itemGroupModifiers.clear();
//$$         itemListModifiers.clear();
//$$         itemLists.clear();
//$$         ItemSwapperBase.LOGGER.info("Processing item groups: " + map.keySet());
//$$         ItemSwapperSharedMod.instance.getItemGroupManager().reset();
//$$         for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
//$$             processEntry(entry);
//$$         }
//$$         applyModifications();
//$$         registerItemGroups();
//$$         itemGroups.clear();
//$$         itemGroupModifiers.clear();
//$$         itemListModifiers.clear();
//$$         itemLists.clear();
//$$     }
//$$
//$$     private void processEntry(Entry<ResourceLocation, JsonElement> entry) {
//$$         try {
//$$             if (!entry.getKey().getNamespace().equals("itemswapper"))
//$$                 return;
//$$             if (entry.getKey().getPath().startsWith("wheel_combined/")) {
//$$                 processCombined(entry.getKey(), entry.getValue());
//$$                 return;
//$$             }
//$$             if (entry.getKey().getPath().startsWith("v2/")) {
//$$                 processV2(entry.getKey(), entry.getValue());
//$$                 return;
//$$             }
//$$             Item[] items = getItemArray(entry.getKey(), entry.getValue(), entry.getKey().getPath().startsWith("wheel"));
//$$             if (items != null) {
//$$                 Builder group = ItemGroup.builder().withId(entry.getKey()).withItems(ItemUtil.toDefault(items));
//$$                 if (entry.getKey().getPath().startsWith("wheel_primary/")) {
//$$                     itemGroups.add(group.withPriority(100));
//$$                 }
//$$                 if (entry.getKey().getPath().startsWith("wheel_secondary/")) {
//$$                     itemGroups.add(group.withPriority(200));
//$$                 }
//$$                 if (entry.getKey().getPath().startsWith("list/")) {
//$$                     itemLists.add(ItemList.builder().withId(entry.getKey()).withItems(items));
//$$                 }
//$$             }
//$$         } catch (Exception ex) {
//$$             ex.printStackTrace();
//$$         }
//$$     }
//$$
//$$     /**
//$$      * Done this way to preserve the load order
//$$      */
//$$     private void registerItemGroups() {
//$$         for (int i = 0; i < itemGroups.size(); i++) {
//$$             ItemSwapperSharedMod.instance.getItemGroupManager()
//$$                     .registerItemGroup(itemGroups.get(i).withItems(filterAir(itemGroups.get(i).getItems())).build());
//$$         }
//$$         for (int i = 0; i < itemLists.size(); i++) {
//$$             ItemSwapperSharedMod.instance.getItemGroupManager().registerListCollection(itemLists.get(i).build());
//$$         }
//$$     }
//$$
//$$     private ItemEntry[] filterAir(ItemEntry[] items) {
//$$         List<ItemEntry> filteredEntries = new ArrayList<ItemEntry>();
//$$         for (ItemEntry entry : items) {
//$$             if (entry.getItem() != Items.AIR) {
//$$                 filteredEntries.add(entry);
//$$             }
//$$         }
//$$         return filteredEntries.toArray(new ItemEntry[0]);
//$$     }
//$$
//$$     /**
//$$      * This entire thing can probably be done a lot smarter and cleaner. But this
//$$      * should work for now
//$$      */
//$$     private void applyModifications() {
//$$         for (int i = 0; i < itemGroupModifiers.size(); i++) {
//$$             ItemGroupModifier modifier = itemGroupModifiers.get(i);
//$$             for (ItemGroup.Builder group : itemGroups) {
//$$                 if (modifier.getTarget().equals(group.getId())) {
//$$                     List<ItemEntry> entries = new ArrayList<>(Arrays.asList(group.getItems()));
//$$                     if (modifier.getRemoveItems() != null) {
//$$                         for (ItemEntry remove : modifier.getRemoveItems()) {
//$$                             entries.removeIf(entry -> (entry.getItem().equals(remove.getItem())));
//$$                         }
//$$                     }
//$$                     if (modifier.getAddItems() != null) {
//$$                         entries.addAll(Arrays.asList(modifier.getAddItems()));
//$$                     }
//$$                     group.withItems(entries.toArray(new ItemEntry[0]));
//$$                     break;
//$$                 }
//$$             }
//$$         }
//$$         for (int i = 0; i < itemListModifiers.size(); i++) {
//$$             ItemListModifier modifier = itemListModifiers.get(i);
//$$             for (ItemList.Builder list : itemLists) {
//$$                 if (modifier.getTarget().equals(list.getId())) {
//$$                     List<Item> entries = new ArrayList<>(Arrays.asList(list.getItems()));
//$$                     if (modifier.getRemoveItems() != null) {
//$$                         for (Item remove : modifier.getRemoveItems()) {
//$$                             entries.removeIf(entry -> (entry.equals(remove)));
//$$                         }
//$$                     }
//$$                     if (modifier.getAddItems() != null) {
//$$                         entries.addAll(Arrays.asList(modifier.getAddItems()));
//$$                     }
//$$                     list.withItems(entries.toArray(new Item[0]));
//$$                     break;
//$$                 }
//$$             }
//$$         }
//$$     }
//$$
//$$     private void processV2(ResourceLocation jsonLocation, JsonElement json) {
//$$         if (!json.isJsonObject()) {
//$$             ItemSwapperBase.LOGGER.warn("Invalid data in " + jsonLocation);
//$$             return;
//$$         }
//$$         JsonObject obj = json.getAsJsonObject();
//$$         String type = obj.get("type").getAsString();
//$$         if (type.equals("palette")) {
//$$             processPalette(jsonLocation, obj);
//$$             return;
//$$         }
//$$         if (type.equals("paletteModification")) {
//$$             processPaletteModification(jsonLocation, obj);
//$$             return;
//$$         }
//$$         if (type.equals("listModification")) {
//$$             processListModification(jsonLocation, obj);
//$$             return;
//$$         }
//$$         if (type.equals("list")) {
//$$             processList(jsonLocation, obj);
//$$             return;
//$$         }
//$$     }
//$$
//$$     private void processList(ResourceLocation jsonLocation, JsonObject json) {
//$$         dev.tr7zw.itemswapper.manager.itemgroups.ItemList.Builder group = ItemList.builder().withId(jsonLocation);
//$$         if (json.has("disableAutoLink") && json.get("disableAutoLink").isJsonPrimitive()) {
//$$             group.withDisableAutoLink(json.get("disableAutoLink").getAsBoolean());
//$$         }
//$$         if (json.has("displayName") && json.get("displayName").isJsonPrimitive()) {
//$$             group.withDisplayName(ComponentProvider.translatable(json.get("displayName").getAsString()));
//$$         }
//$$         if (json.has("link") && json.get("link").isJsonPrimitive()) {
//$$             try {
//$$                 group.withLink(new ResourceLocation(json.getAsJsonPrimitive("link").getAsString()));
//$$             } catch (Exception ex) {
//$$                 ItemSwapperBase.LOGGER.warn("Invalid link in " + jsonLocation);
//$$             }
//$$         }
//$$         group.withItems(getItemArray(jsonLocation, json.get("items"), false));
//$$         Item[] openOnly = getItemArray(jsonLocation, json.get("openOnlyItems"), false);
//$$         if (openOnly != null && openOnly.length > 0) {
//$$             group.withOpenOnlyItems(new HashSet<>(Arrays.asList(openOnly)));
//$$         }
//$$         Item[] ignoreItems = getItemArray(jsonLocation, json.get("ignoreItems"), false);
//$$         if (ignoreItems != null && ignoreItems.length > 0) {
//$$             group.withIgnoreItems(new HashSet<>(Arrays.asList(ignoreItems)));
//$$         }
//$$         if (json.has("icon") && json.get("icon").isJsonPrimitive()) {
//$$             group.withIcon(BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("icon").getAsString())));
//$$         }
//$$         itemLists.add(group);
//$$     }
//$$
//$$     private void processPalette(ResourceLocation jsonLocation, JsonObject json) {
//$$         Builder group = ItemGroup.builder().withId(jsonLocation);
//$$         if (json.has("priority") && json.get("priority").isJsonPrimitive()) {
//$$             group.withPriority(json.getAsJsonPrimitive("priority").getAsInt());
//$$         } else {
//$$             group.withPriority(100);
//$$         }
//$$         if (json.has("fallbackLink") && json.get("fallbackLink").isJsonPrimitive()) {
//$$             try {
//$$                 group.withFallbackLink(new ResourceLocation(json.getAsJsonPrimitive("fallbackLink").getAsString()));
//$$             } catch (Exception ex) {
//$$                 ItemSwapperBase.LOGGER.warn("Invalid fallbackLink in " + jsonLocation);
//$$             }
//$$         }
//$$         if (json.has("forceLink") && json.get("forceLink").isJsonPrimitive()) {
//$$             try {
//$$                 group.withForcedLink(new ResourceLocation(json.getAsJsonPrimitive("forceLink").getAsString()));
//$$             } catch (Exception ex) {
//$$                 ItemSwapperBase.LOGGER.warn("Invalid forceLink in " + jsonLocation);
//$$             }
//$$         }
//$$         if (json.has("disableAutoLink") && json.get("disableAutoLink").isJsonPrimitive()) {
//$$             group.withDisableAutoLink(json.get("disableAutoLink").getAsBoolean());
//$$         }
//$$         if (json.has("displayName") && json.get("displayName").isJsonPrimitive()) {
//$$             group.withDisplayName(ComponentProvider.translatable(json.get("displayName").getAsString()));
//$$         }
//$$         group.withItems(processItems(jsonLocation, json.get("items")));
//$$         Item[] openOnly = getItemArray(jsonLocation, json.get("openOnlyItems"), false);
//$$         if (openOnly != null && openOnly.length > 0) {
//$$             group.withOpenOnlyItems(new HashSet<>(Arrays.asList(openOnly)));
//$$         }
//$$         Item[] ignoreItems = getItemArray(jsonLocation, json.get("ignoreItems"), false);
//$$         if (ignoreItems != null && ignoreItems.length > 0) {
//$$             group.withIgnoreItems(new HashSet<>(Arrays.asList(ignoreItems)));
//$$         }
//$$         group.withShortcuts(processShortcuts(jsonLocation, json.get("shortcuts")));
//$$         if (json.has("icon") && json.get("icon").isJsonPrimitive()) {
//$$             group.withIcon(BuiltInRegistries.ITEM.get(new ResourceLocation(json.get("icon").getAsString())));
//$$         }
//$$         itemGroups.add(group);
//$$     }
//$$
//$$     private void processListModification(ResourceLocation jsonLocation, JsonObject json) {
//$$         dev.tr7zw.itemswapper.manager.itemgroups.ItemListModifier.Builder changes = ItemListModifier.builder();
//$$         if (json.has("target") && json.get("target").isJsonPrimitive()) {
//$$             try {
//$$                 changes.withTarget(new ResourceLocation(json.getAsJsonPrimitive("target").getAsString()));
//$$             } catch (Exception ex) {
//$$                 ItemSwapperBase.LOGGER.warn("Invalid target in " + jsonLocation);
//$$                 return;
//$$             }
//$$         }
//$$         changes.withAddItems(getItemArray(jsonLocation, json.get("addItems"), false));
//$$         changes.withRemoveItems(getItemArray(jsonLocation, json.get("removeItems"), false));
//$$         itemListModifiers.add(changes.build());
//$$     }
//$$
//$$     private void processPaletteModification(ResourceLocation jsonLocation, JsonObject json) {
//$$         dev.tr7zw.itemswapper.manager.itemgroups.ItemGroupModifier.Builder changes = ItemGroupModifier.builder();
//$$         if (json.has("target") && json.get("target").isJsonPrimitive()) {
//$$             try {
//$$                 changes.withTarget(new ResourceLocation(json.getAsJsonPrimitive("target").getAsString()));
//$$             } catch (Exception ex) {
//$$                 ItemSwapperBase.LOGGER.warn("Invalid target in " + jsonLocation);
//$$                 return;
//$$             }
//$$         }
//$$         changes.withAddItems(processItems(jsonLocation, json.get("addItems")));
//$$         changes.withRemoveItems(processItems(jsonLocation, json.get("removeItems")));
//$$         itemGroupModifiers.add(changes.build());
//$$     }
//$$
//$$     private List<Shortcut> processShortcuts(ResourceLocation jsonLocation, JsonElement object) {
//$$         if (object == null || !object.isJsonArray()) {
//$$             return Collections.emptyList();
//$$         }
//$$         List<Shortcut> shortcuts = new ArrayList<>();
//$$         object.getAsJsonArray().forEach(el -> {
//$$             if (!el.isJsonObject()) {
//$$                 return;
//$$             }
//$$             JsonObject entry = el.getAsJsonObject();
//$$             if (entry.has("type") && entry.get("type").isJsonPrimitive()
//$$                     && "link".equals(entry.get("type").getAsString())) {
//$$                 String displayname = entry.has("displayName") && entry.get("displayName").isJsonPrimitive()
//$$                         ? entry.get("displayName").getAsString()
//$$                         : null;
//$$                 Item icon = null;
//$$                 if (entry.has("icon") && entry.get("icon").isJsonPrimitive()) {
//$$                     icon = BuiltInRegistries.ITEM.get(new ResourceLocation(entry.get("icon").getAsString()));
//$$                 }
//$$                 try {
//$$                     shortcuts.add(
//$$                             new LinkShortcut(new ResourceLocation(entry.getAsJsonPrimitive("target").getAsString()),
//$$                                     displayname != null ? ComponentProvider.translatable(displayname) : null, icon));
//$$                 } catch (Exception ex) {
//$$                     ItemSwapperBase.LOGGER.warn("Invalid link target shortcut in " + jsonLocation);
//$$                 }
//$$             }
//$$         });
//$$         return shortcuts;
//$$     }
//$$
//$$     private ItemEntry[] processItems(ResourceLocation jsonLocation, JsonElement object) {
//$$         if (object == null || !object.isJsonArray()) {
//$$             return null;
//$$         }
//$$         List<ItemEntry> itemList = new ArrayList<>();
//$$         object.getAsJsonArray().forEach(el -> {
//$$             if (el.isJsonPrimitive()) {
//$$                 ResourceLocation resourceLocation = new ResourceLocation(el.getAsString());
//$$                 Item item = BuiltInRegistries.ITEM.get(resourceLocation);
//$$                 if (item == Items.AIR) {
//$$                     ItemSwapperBase.LOGGER.info("Unable to find " + resourceLocation + ", ignoring.");
//$$                 }
//$$                 ItemEntry entry = new ItemEntry(item, null);
//$$                 if (!itemList.contains(entry)) {
//$$                     itemList.add(entry);
//$$                 }
//$$             }
//$$             if (el.isJsonObject()) {
//$$                 JsonObject obj = el.getAsJsonObject();
//$$                 ResourceLocation resourceLocation = new ResourceLocation(obj.get("id").getAsString());
//$$                 Item item = BuiltInRegistries.ITEM.get(resourceLocation);
//$$                 if (item == Items.AIR) {
//$$                     ItemSwapperBase.LOGGER.info("Unable to find " + resourceLocation + ", ignoring.");
//$$                 }
//$$                 ResourceLocation link = null;
//$$                 if (obj.has("link") && obj.get("link").isJsonPrimitive()) {
//$$                     try {
//$$                         link = new ResourceLocation(obj.get("link").getAsString());
//$$                     } catch (Exception ex) {
//$$                         ItemSwapperBase.LOGGER.warn("Invalid item link in " + jsonLocation);
//$$                     }
//$$                 }
//$$                 String displayName = null;
//$$                 if (obj.has("name") && obj.get("name").isJsonPrimitive()) {
//$$                     displayName = obj.getAsJsonPrimitive("name").getAsString();
//$$                 }
//$$                 boolean actAsLink = false;
//$$                 if (obj.has("actAsLink") && obj.get("actAsLink").isJsonPrimitive()) {
//$$                     actAsLink = obj.getAsJsonPrimitive("actAsLink").getAsBoolean();
//$$                 }
//$$                 ItemEntry entry = new ItemEntry(item, link,
//$$                         displayName != null ? ComponentProvider.translatable(displayName) : null, actAsLink);
//$$                 if (!itemList.contains(entry)) {
//$$                     itemList.add(entry);
//$$                 }
//$$             }
//$$         });
//$$         if (!itemList.isEmpty()) {
//$$             return itemList.toArray(new ItemEntry[0]);
//$$         }
//$$         return null;
//$$     }
//$$
//$$     private void processCombined(ResourceLocation jsonLocation, JsonElement json) {
//$$         if (json == null || !json.isJsonArray()) {
//$$             return;
//$$         }
//$$         JsonArray ar = json.getAsJsonArray();
//$$         List<Item[]> lists = new ArrayList<>();
//$$         for (int i = 0; i < ar.size(); i++) {
//$$             Item[] list = getItemArray(jsonLocation, ar.get(i), true);
//$$             if (list != null && list.length > 0) {
//$$                 lists.add(list);
//$$             }
//$$         }
//$$         if (lists.isEmpty()) {
//$$             return;
//$$         }
//$$         for (int i = 0; i < lists.size(); i++) {
//$$             ResourceLocation ownId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + i);
//$$             int next = i + 1 == lists.size() ? 0 : i + 1;
//$$             ResourceLocation nextId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + next);
//$$             itemGroups.add(
//$$                     ItemGroup.builder().withId(ownId).withForcedLink(nextId).withItems(ItemUtil.toDefault(lists.get(i)))
//$$                             .withShortcuts(Arrays.asList(new LinkShortcut(nextId))));
//$$         }
//$$     }
//$$
//$$     private Item[] getItemArray(ResourceLocation jsonLocation, JsonElement json, boolean pallet) {
//$$         if (json == null || !json.isJsonArray()) {
//$$             return null;
//$$         }
//$$         List<Item> itemList = new ArrayList<>();
//$$         json.getAsJsonArray().forEach(el -> {
//$$             if (el.isJsonPrimitive()) {
//$$                 ResourceLocation resourceLocation = new ResourceLocation(el.getAsString());
//$$                 Item item = BuiltInRegistries.ITEM.get(resourceLocation);
//$$                 if (item.equals(Items.AIR)) {
//$$                     ItemSwapperBase.LOGGER.warn("Unknown item: " + el.getAsString() + " in " + jsonLocation);
//$$                     if (pallet) {
//$$                         // For unknown items, don't move the rest of the wheel
//$$                         itemList.add(Items.AIR);
//$$                     }
//$$                     return;
//$$                 }
//$$                 if (!itemList.contains(item)) {
//$$                     itemList.add(item);
//$$                 }
//$$             }
//$$         });
//$$         if (!itemList.isEmpty()) {
//$$             return itemList.toArray(new Item[0]);
//$$         }
//$$         return null;
//$$     }
//$$
//$$ }
//$$ #endif
//$$ spotless:on

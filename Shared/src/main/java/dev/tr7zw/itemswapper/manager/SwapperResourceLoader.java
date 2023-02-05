package dev.tr7zw.itemswapper.manager;

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

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup.Builder;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LinkShortcut;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
            ProfilerFiller profilerFiller) {
        ItemSwapperSharedMod.LOGGER.info("Processing item groups: " + map.keySet());
        ItemSwapperSharedMod.instance.getItemGroupManager().reset();
        for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
                if (!entry.getKey().getNamespace().equals("itemswapper"))
                    continue;
                if (entry.getKey().getPath().startsWith("wheel_combined/")) {
                    processCombined(entry.getKey(), entry.getValue());
                    continue;
                }
                if (entry.getKey().getPath().startsWith("v2/")) {
                    processV2(entry.getKey(), entry.getValue());
                    continue;
                }
                Item[] items = getItemArray(entry.getKey(), entry.getValue(),
                        entry.getKey().getPath().startsWith("wheel"));
                if (items != null) {
                    Builder group = ItemGroup.builder().withId(entry.getKey()).withItems(ItemUtil.toDefault(items));
                    if (entry.getKey().getPath().startsWith("wheel_primary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager()
                                .registerItemGroup(group.withPriority(100).build());
                    }
                    if (entry.getKey().getPath().startsWith("wheel_secondary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager()
                                .registerItemGroup(group.withPriority(200).build());
                    }
                    if (entry.getKey().getPath().startsWith("list/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerListCollection(entry.getKey(),
                                items);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void processV2(ResourceLocation jsonLocation, JsonElement json) {
        if (!json.isJsonObject()) {
            ItemSwapperSharedMod.LOGGER.warn("Invalid data in " + jsonLocation);
            return;
        }
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        if (type.equals("palette")) {
            processPalette(jsonLocation, obj);
            return;
        }
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
                group.withFallbackLink(new ResourceLocation(json.getAsJsonPrimitive("fallbackLink").getAsString()));
            } catch (Exception ex) {
                ItemSwapperSharedMod.LOGGER.warn("Invalid fallbackLink in " + jsonLocation);
            }
        }
        if (json.has("forceLink") && json.get("forceLink").isJsonPrimitive()) {
            try {
                group.withForcedLink(new ResourceLocation(json.getAsJsonPrimitive("forceLink").getAsString()));
            } catch (Exception ex) {
                ItemSwapperSharedMod.LOGGER.warn("Invalid forceLink in " + jsonLocation);
            }
        }
        if (json.has("disableAutoLink") && json.get("disableAutoLink").isJsonPrimitive()) {
            group.withDisableAutoLink(json.get("disableAutoLink").getAsBoolean());
        }
        if (json.has("displayName") && json.get("displayName").isJsonPrimitive()) {
            group.withDisplayName(Component.translatable(json.get("displayName").getAsString()));
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
        ItemSwapperSharedMod.instance.getItemGroupManager().registerItemGroup(group.build());
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
                try {
                    shortcuts.add(
                            new LinkShortcut(new ResourceLocation(entry.getAsJsonPrimitive("target").getAsString()),
                                    displayname != null ? Component.translatable(displayname) : null));
                } catch (Exception ex) {
                    ItemSwapperSharedMod.LOGGER.warn("Invalid link target shortcut in " + jsonLocation);
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
                ResourceLocation resourceLocation = new ResourceLocation(el.getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                ItemEntry entry = new ItemEntry(item, null);
                if (!itemList.contains(entry)) {
                    itemList.add(entry);
                }
            }
            if (el.isJsonObject()) {
                JsonObject obj = el.getAsJsonObject();
                ResourceLocation resourceLocation = new ResourceLocation(obj.get("id").getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                ResourceLocation link = null;
                try {
                    link = new ResourceLocation(obj.get("link").getAsString());
                } catch (Exception ex) {
                    ItemSwapperSharedMod.LOGGER.warn("Invalid item link in " + jsonLocation);
                }
                String displayName = null;
                if (obj.has("name") && obj.get("name").isJsonPrimitive()) {
                    displayName = obj.getAsJsonPrimitive("name").getAsString();
                }
                ItemEntry entry = new ItemEntry(item, link,
                        displayName != null ? Component.translatable(displayName) : null);
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
            ResourceLocation ownId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + i);
            int next = i + 1 == lists.size() ? 0 : i + 1;
            ResourceLocation nextId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + next);
            ItemSwapperSharedMod.instance.getItemGroupManager()
                    .registerItemGroup(ItemGroup.builder().withId(ownId).withForcedLink(nextId)
                            .withItems(ItemUtil.toDefault(lists.get(i))).withShortcuts(Arrays
                                    .asList(new LinkShortcut(nextId)))
                            .build());
        }
    }

    private Item[] getItemArray(ResourceLocation jsonLocation, JsonElement json, boolean pallet) {
        if (json == null || !json.isJsonArray()) {
            return null;
        }
        List<Item> itemList = new ArrayList<>();
        json.getAsJsonArray().forEach(el -> {
            if (el.isJsonPrimitive()) {
                ResourceLocation resourceLocation = new ResourceLocation(el.getAsString());
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                if (item.equals(Items.AIR)) {
                    ItemSwapperSharedMod.LOGGER.warn("Unknown item: " + el.getAsString() + " in " + jsonLocation);
                    if (pallet) {
                        // For unknown items, don't move the rest of the wheel
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

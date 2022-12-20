package dev.tr7zw.itemswapper.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import net.minecraft.core.Registry;
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
                if (entry.getKey().getPath().startsWith("wheel_combined")) {
                    processCombined(entry.getKey(), entry.getValue());
                    continue;
                }
                Item[] items = getItemArray(entry.getKey(), entry.getValue(),
                        entry.getKey().getPath().startsWith("wheel"));
                if (items != null) {
                    if (entry.getKey().getPath().startsWith("wheel_primary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerCollection(items);
                    }
                    if (entry.getKey().getPath().startsWith("wheel_secondary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerSecondaryCollection(items);
                    }
                    if (entry.getKey().getPath().startsWith("list/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerListCollection(items);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void processCombined(ResourceLocation jsonLocation, JsonElement json) {
        if (!json.isJsonArray()) {
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
        ItemSwapperSharedMod.instance.getItemGroupManager().registerCollections(lists.toArray(new Item[0][]));
    }

    private Item[] getItemArray(ResourceLocation jsonLocation, JsonElement json, boolean pallet) {
        if (!json.isJsonArray()) {
            return null;
        }
        List<Item> itemList = new ArrayList<>();
        json.getAsJsonArray().forEach(el -> {
            if (el.isJsonPrimitive()) {
                ResourceLocation resourceLocation = new ResourceLocation(el.getAsString());
                Item item = Registry.ITEM.get(resourceLocation);
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

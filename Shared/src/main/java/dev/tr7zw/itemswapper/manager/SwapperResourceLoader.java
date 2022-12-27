package dev.tr7zw.itemswapper.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ClearCurrentSlotShortcut;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup.Builder;
import dev.tr7zw.itemswapper.util.ItemUtil;
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
                    Builder group = ItemGroup.builder().withId(entry.getKey()).withItems(ItemUtil.toDefault(items));
                    if (entry.getKey().getPath().startsWith("wheel_primary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerItemGroup(group.withPriority(100).withRightSideShortcuts(Arrays.asList(ClearCurrentSlotShortcut.INSTANCE)).build());
                    }
                    if (entry.getKey().getPath().startsWith("wheel_secondary/")) {
                        ItemSwapperSharedMod.instance.getItemGroupManager().registerItemGroup(group.withPriority(200).withRightSideShortcuts(Arrays.asList(ClearCurrentSlotShortcut.INSTANCE)).build());
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
        for(int i = 0; i < lists.size(); i++) {
            ResourceLocation ownId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + i);
            int next = i+1 == lists.size() ? 0 : i+1;
            ResourceLocation nextId = new ResourceLocation(jsonLocation.getNamespace(), jsonLocation.getPath() + next);
            ItemSwapperSharedMod.instance.getItemGroupManager().registerItemGroup(ItemGroup.builder().withId(ownId).withForcedLink(nextId).withItems(ItemUtil.toDefault(lists.get(i))).withRightSideShortcuts(Arrays.asList(ClearCurrentSlotShortcut.INSTANCE)).build());
        }
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

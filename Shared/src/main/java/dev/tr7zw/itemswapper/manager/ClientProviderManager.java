package dev.tr7zw.itemswapper.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.api.client.ItemProvider;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClientProviderManager {

    private final Minecraft minecraft = Minecraft.getInstance();
    private Set<ItemProvider> earlyItemProvider = new HashSet<>();
    private Set<ItemProvider> lateItemProvider = new HashSet<>();
    private Map<Item, ContainerProvider> containerProvider = new HashMap<>();
    private Map<Item, NameProvider> nameProvider = new HashMap<>();

    public void registerEarlyItemProvider(ItemProvider provider) {
        earlyItemProvider.add(provider);
    }

    public void registerLateItemProvider(ItemProvider provider) {
        lateItemProvider.add(provider);
    }

    public void registerContainerProvider(ContainerProvider provider) {
        for (Item item : provider.getItemHandlers()) {
            containerProvider.put(item, provider);
        }
    }

    public void registerNameProvider(NameProvider provider) {
        for (Item item : provider.getItemHandlers()) {
            nameProvider.put(item, provider);
        }
    }

    public Set<ItemProvider> getEarlyItemProvider() {
        return earlyItemProvider;
    }

    public Set<ItemProvider> getLateItemProvider() {
        return lateItemProvider;
    }

    public ContainerProvider getContainerProvider(Item item) {
        return containerProvider.get(item);
    }

    public NameProvider getNameProvider(Item item) {
        return nameProvider.get(item);
    }

    public List<AvailableSlot> findSlotsMatchingItem(Item item, boolean limit, boolean ignoreHotbar) {
        NonNullList<ItemStack> items = minecraft.player.getInventory().items;
        List<AvailableSlot> ids = new ArrayList<>();
        handleProvider(getEarlyItemProvider(), item, limit, ids);
        if (limit && !ids.isEmpty()) {
            return ids;
        }
        for (int i = ignoreHotbar ? 9 : 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty() && item != Items.AIR) {
                continue;
            }
            if (itemStack.getItem() == item) {
                addUnstackableItems(ids, new AvailableSlot(-1, i, items.get(i)));
                if (limit) {
                    return ids;
                }
                continue;
            }
            ContainerProvider provider = getContainerProvider(itemStack.getItem());
            if (provider != null) {
                handleSlotlist(ids, limit, provider.processItemStack(itemStack, item, limit, i));
                if (limit && !ids.isEmpty()) {
                    return ids;
                }
            }
        }
        handleProvider(getLateItemProvider(), item, limit, ids);
        return ids;
    }

    private void handleProvider(Set<ItemProvider> providers, Item item, boolean limit, List<AvailableSlot> ids) {
        for (ItemProvider provider : providers) {
            List<AvailableSlot> found = provider.findSlotsMatchingItem(item, limit);
            handleSlotlist(ids, limit, found);
        }
    }

    private void handleSlotlist(List<AvailableSlot> ids, boolean limit, List<AvailableSlot> found) {
        for (AvailableSlot slot : found) {
            addUnstackableItems(ids, slot);
            if (limit) {
                return;
            }
        }
    }

    private void addUnstackableItems(List<AvailableSlot> ids, AvailableSlot slot) {
        for (AvailableSlot s : ids) {
            if (ItemStack.isSameItemSameTags(s.item(), slot.item())) {
                s.amount().accumulateAndGet(slot.item().getCount(), (i1, i2) -> i1 + i2);
                return;
            }
        }
        ids.add(slot);
    }

}

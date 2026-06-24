package dev.tr7zw.itemswapper.server.manger;

import dev.tr7zw.itemswapper.api.*;
import dev.tr7zw.itemswapper.api.server.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;

public class ServerProviderManager {

    private final Map<String, ServerItemContainerProvider> idContainerProvider = new HashMap<>();
    private final Map<Item, ServerItemContainerProvider> containerProvider = new HashMap<>();

    public void registerProvider(ServerItemContainerProvider provider) {
        idContainerProvider.put(provider.getId(), provider);
        for (Item item : provider.getItemHandlers()) {
            containerProvider.put(item, provider);
        }
    }

    public List<RemoteItem> findRemoteItems(ServerPlayer player, Set<Item> searchItems) {
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(player.getInventory());
        List<RemoteItem> ids = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            ServerItemContainerProvider provider = getContainerProvider(itemStack.getItem());
            if (provider != null) {
                NonNullList<RemoteItem> contents = provider.getItemStacks(player, itemStack, i);
                ids.addAll(contents.stream().filter(stack -> searchItems.contains(stack.itemStack().getItem()))
                        .sorted(Comparator.comparing(RemoteItem::slot).thenComparing(Comparator.comparingInt(RemoteItem::id).reversed())).toList());
            }
        }
        return ids;
    }

    private ServerItemContainerProvider getContainerProvider(Item item) {
        return containerProvider.get(item);
    }

    /**
     *
     * @param player
     * @param targetItem
     * @param itemStack
     * @return amount stored away, 0 if nothing was stored or the provider doesn't
     *         exist
     */
    public int insertItem(ServerPlayer player, RemoteItem targetItem, ItemStack itemStack) {
        ServerItemContainerProvider provider = idContainerProvider.get(targetItem.providerId());
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(player.getInventory());
        ItemStack container = items.get(targetItem.slot());
        // make sure we have the right provider and that the provider fits to the itemstack
        if (provider != null && provider == getContainerProvider(container.getItem())) {
            return provider.insertItem(player, container, itemStack);
        }
        return 0;
    }

    public boolean putIntoSlot(ServerPlayer player, RemoteItem remoteItem, int inventorySlot) {
        ServerItemContainerProvider provider = idContainerProvider.get(remoteItem.providerId());
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(player.getInventory());
        ItemStack container = items.get(remoteItem.slot());
        if (!items.get(inventorySlot).isEmpty() || container.isEmpty() || provider == null
                || provider != getContainerProvider(container.getItem())) {
            return false;
        }
        ItemStack inventoryItem = items.get(inventorySlot);
        if (inventoryItem.isEmpty()) {
            // empty slot, just get item out
            ItemStack removedItem = provider.removeItem(player, container, remoteItem);
            if (removedItem != null) {
                player.getInventory().setItem(inventorySlot, removedItem);
                return true;
            }
        }
        return false;
    }

    public int takeFromSlot(ServerPlayer player, RemoteItem remoteItem, int toTake) {
        ServerItemContainerProvider provider = idContainerProvider.get(remoteItem.providerId());
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(player.getInventory());
        ItemStack container = items.get(remoteItem.slot());
        if (container.isEmpty() || provider == null || provider != getContainerProvider(container.getItem())) {
            return 0;
        }
        return provider.takeFromSlot(player, container, remoteItem, toTake);
    }
}

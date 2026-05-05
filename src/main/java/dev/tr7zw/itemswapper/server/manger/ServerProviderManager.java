package dev.tr7zw.itemswapper.server.manger;

import dev.tr7zw.itemswapper.api.*;
import dev.tr7zw.itemswapper.api.client.*;
import dev.tr7zw.itemswapper.api.server.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;

public class ServerProviderManager {

    private final Map<Item, ServerItemContainerProvider> containerProvider = new HashMap<>();

    public void registerProvider(ServerItemContainerProvider provider) {
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
                NonNullList<RemoteItem> contents = provider.getItemStacks(itemStack, i);
                for (RemoteItem item : contents) {
                    if (searchItems.contains(item.itemStack().getItem())) {
                        ids.add(item);
                    }
                }
            }
        }
        return ids;
    }

    private ServerItemContainerProvider getContainerProvider(Item item) {
        return containerProvider.get(item);
    }

}

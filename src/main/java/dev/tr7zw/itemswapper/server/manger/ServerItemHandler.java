package dev.tr7zw.itemswapper.server.manger;

import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.packets.clientbound.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.transition.config.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import lombok.*;
import net.minecraft.world.item.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

@RequiredArgsConstructor
public class ServerItemHandler {

    private static final Logger network_logger = LogManager.getLogger("ItemSwapper-Network");
    private static final ConfigManager<Config> configManager = ConfigHolder.getInstance().getGeneral();
    private final ServerProviderManager providerManager;
    private final ServerPlayerManager playerManager;

    public void swapItem(ServerPlayer player, SwapItemPayload payload) {
        if (configManager.getConfig().disableShulkers) {
            // no refill allowed
            return;
        }
        try {
            NonNullList<RemoteItem> contents = providerManager.contentsOf(player, payload.inventorySlot());
            if (contents == null) {
                return;
            }
            RemoteItem target = null;
            for (RemoteItem remoteItem : contents) {
                if (remoteItem.id() == payload.slot()) {
                    target = remoteItem;
                    break;
                }
            }
            if (target == null) {
                return;
            }
            int selected = InventoryUtil.getSelectedId(player.getInventory());
            ItemStack taken = providerManager.exchangeSlot(player, target,
                    InventoryUtil.getSelected(player.getInventory()));
            if (taken != null) {
                player.getInventory().setItem(selected, taken);
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    public boolean storeAwayItem(ServerPlayer player, int slot, Set<Item> itemSet) {
        ItemStack slotItem = player.getInventory().getItem(slot);
        // Try putting the item to a matching stack first
        Integer amount = storeToItem(player, slotItem, Collections.singleton(slotItem.getItem()));
        if (amount <= 0)
            return true;
        // try finding fitting similar items
        if (!itemSet.isEmpty()) {
            amount = storeToItem(player, slotItem, itemSet);
            if (amount <= 0)
                return true;
        }
        // did not find a suitable slot in a matching container, try to put it in any container that can accept it
        List<RemoteItem> anySlots = providerManager.findRemoteItems(player, Collections.singleton(Items.AIR));
        for (RemoteItem remoteItem : anySlots) {
            int inserted = providerManager.insertItem(player, remoteItem, slotItem);
            slotItem.shrink(inserted);
            amount -= inserted;
            if (amount <= 0) {
                return true;
            }
        }
        return false;
    }

    private Integer storeToItem(ServerPlayer player, ItemStack slotItem, Set<Item> targetTypes) {
        List<RemoteItem> fittinSlots = providerManager.findRemoteItems(player, targetTypes);
        int amount = slotItem.count();
        for (RemoteItem remoteItem : fittinSlots) {
            int inserted = providerManager.insertItem(player, remoteItem, slotItem);
            slotItem.shrink(inserted);
            amount -= inserted;
            if (amount <= 0) {
                return amount;
            }
        }
        return amount;
    }

    public void refillSlot(ServerPlayer player, RefillItemPayload payload) {
        if (configManager.getConfig().disableShulkers) {
            // no refill allowed
            return;
        }
        try {
            ItemStack target = player.getInventory().getItem(payload.slot());
            if (target == null || target.isEmpty()) {
                return;
            }
            int space = target.getMaxStackSize() - target.count();
            if (space <= 0) {
                // nothing to do
                return;
            }
            var remoteItems = providerManager.findRemoteItems(player, Collections.singleton(target.getItem()));
            for (RemoteItem remoteItem : remoteItems) {
                if (remoteItem.count() <= 0) {
                    continue;
                }
                int toTake = Math.min(space, remoteItem.count());
                int taken = providerManager.takeFromSlot(player, remoteItem, toTake);
                if (taken > 0) {
                    target.grow(taken);
                    space -= taken;
                    if (space <= 0) {
                        break;
                    }
                }
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    public void openContainer(ServerPlayer player, RequestContainerPayload payload) {
        NonNullList<RemoteItem> contents = configManager.getConfig().disableShulkers ? null
                : providerManager.contentsOf(player, payload.slot());
        boolean container = contents != null;
        ServerNetworkUtil.sendPacket(player, new ContainerContentPayload(payload.requestId(), payload.slot(), container,
                container ? contents : List.of()));
    }

    public void exchangeContainerSlot(ServerPlayer player, ExchangeContainerSlotPayload payload) {
        if (configManager.getConfig().disableShulkers) {
            return;
        }
        int selected = payload.selectedSlot();
        if (selected < 0 || selected > 8) {
            return;
        }
        try {
            ItemStack hand = player.getInventory().getItem(selected);
            ItemStack taken = providerManager.exchangeSlot(player, payload.remoteItem(), hand);
            if (taken != null) {
                player.getInventory().setItem(selected, taken);
            }
        } catch (Throwable th) {
            network_logger.error("Error handling network packet!", th);
        }
    }

    public void processAvailability(ServerPlayer player, RequestAvailability payload) {
        List<RemoteItem> items = providerManager.findRemoteItems(player, payload.itemListing().asItemSet());
        // Empty slots encode as a blank string. Older clients cannot read that on this packet.
        items.removeIf(remoteItem -> remoteItem.itemStack().isEmpty());
        ServerNetworkUtil.sendPacket(player, new ItemAvailability(items));
    }

    public void switchToItem(ServerPlayer player, SwitchToItemPayload payload) {
        if (payload.inventorySlot() < 0 || payload.inventorySlot() >= player.getInventory().getContainerSize()) {
            return;
        }
        providerManager.putIntoSlot(player, payload.remoteItem(), payload.inventorySlot());
    }

    public void switchToAnyItem(ServerPlayer player, RequestAnyItemPayload payload) {
        if (payload.item() == null || payload.item() == Items.AIR) {
            return;
        }
        List<RemoteItem> items = providerManager.findRemoteItems(player, Collections.singleton(payload.item()));
        if (items.isEmpty()) {
            return;
        }
        var inv = InventoryUtil.getInventory(player);
        if (!InventoryUtil.getSelected(inv).isEmpty() && !storeAwayItem(player, InventoryUtil.getSelectedId(inv),
                payload.emptySlotPayload().itemListing().asItemSet())) {
            // Slot needs to be empty
            return;
        }
        providerManager.putIntoSlot(player, items.get(0), InventoryUtil.getSelectedId(inv));

    }
}

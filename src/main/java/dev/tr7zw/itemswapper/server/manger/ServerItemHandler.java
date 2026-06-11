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

import dev.tr7zw.itemswapper.util.ShulkerHelper;
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
            if (ShulkerHelper.isShulker(InventoryUtil.getSelected(player.getInventory()).getItem())) {
                // Don't try to put a shulker into another shulker
                return;
            }
            ItemStack shulker = player.getInventory().getItem(payload.inventorySlot());
            NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
            if (content != null) {
                ItemStack tmp = content.get(payload.slot());
                storeAwayItem(player, InventoryUtil.getSelectedId(player.getInventory()), Collections.emptySet());
                content.set(payload.slot(), InventoryUtil.getSelected(player.getInventory()));
                player.getInventory().setItem(InventoryUtil.getSelectedId(player.getInventory()), tmp);
                ShulkerHelper.setItem(shulker, content);
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
            // TODO: switch to provider system
            PlayerSession session = playerManager.getSession(player);
            for (int i = 0; i < InventoryUtil.getNonEquipmentItems(player.getInventory()).size(); i++) {
                ItemStack shulker = InventoryUtil.getNonEquipmentItems(player.getInventory()).get(i);
                NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
                if (content != null) {
                    boolean boxChanged = false;
                    for (int entry = 0; entry < content.size(); entry++) {
                        ItemStack boxItem = content.get(entry);
                        if (ServerItemUtil.isSame(boxItem, target)) {
                            // same, use to restock
                            // if keep last item is enabled, leave one item in the box to prevent accidentally emptying it
                            int backup = session.isKeepLastItem() ? 1 : 0;
                            int amount = Math.min(space, boxItem.count() - backup);
                            target.setCount(target.count() + amount);
                            boxItem.setCount(boxItem.count() - amount);
                            space -= amount;
                            boxChanged = true;
                            if (space <= 0) {
                                break;
                            }
                        }
                    }
                    if (boxChanged) {
                        ShulkerHelper.setItem(shulker, content);
                    }
                }
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    public void processAvailability(ServerPlayer player, RequestAvailability payload) {
        List<RemoteItem> items = providerManager.findRemoteItems(player, payload.itemListing().asItemSet());
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

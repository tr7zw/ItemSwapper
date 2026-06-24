package dev.tr7zw.itemswapper.manager;

import dev.tr7zw.itemswapper.api.*;
import dev.tr7zw.itemswapper.api.client.*;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import lombok.*;
import net.minecraft.client.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.concurrent.atomic.*;

@RequiredArgsConstructor
public class ItemManager {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ClientProviderManager providerManager;
    private final ItemSwapperClientAPI clientAPI;
    private final ItemGroupManager itemGroupManager;

    public boolean grabLocalItem(Item item, boolean ignoreHotbar) {
        List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(item, false, ignoreHotbar);
        for (AvailableSlot slot : slots) {
            if (slot.inventory() != -1) {
                // Not in player inventory, so can't grab from there
                continue;
            }
            return grabLocalItem(slot);
        }
        ClientNetworkUtil.sendPacket(new RequestAnyItemPayload(item, createEmptySlotPayload(
                InventoryUtil.getSelectedId(InventoryUtil.getInventory(GeneralUtil.getPlayer())))));
        return false;
    }

    public boolean grabLocalItem(AvailableSlot slot) {
        // Only grab from player inventory, which is -1
        if (slot.inventory() != -1) {
            return false;
        }
        ItemSwapperClientAPI.OnSwap event = clientAPI.prepareItemSwapEvent
                .callEvent(new ItemSwapperClientAPI.OnSwap(slot, new AtomicBoolean()));
        if (event.canceled().get()) {
            // interaction canceled by some other mod
            return false;
        }
        ItemUtil.swapWithSlot(ItemUtil.inventorySlotToHudSlot(slot.slot()));
        clientAPI.itemSwapSentEvent.callEvent(new ItemSwapperClientAPI.SwapSent(slot));
        return true;
    }

    public void sendEmptySlotPayload(int slot) {
        ClientNetworkUtil.sendPacket(createEmptySlotPayload(slot));
    }

    private EmptySlotPayload createEmptySlotPayload(int slot) {
        ItemStack item = minecraft.player.getInventory().getItem(slot);
        List<Item> items = new ArrayList<>();
        if (!item.isEmpty()) {
            List<ItemGroup> groups = itemGroupManager.getItemPages(item.getItem());
            if (ConfigHolder.getInstance().getGeneral().getConfig().sortWithAllPalettes) {
                for (ItemGroup group : groups) {
                    items.addAll(Arrays.asList(group.items()).stream().map(ItemEntry::getItem)
                            .filter(i -> !items.contains(i)).toList());
                }
            } else {
                if (!groups.isEmpty()) {
                    items.addAll(Arrays.asList(groups.get(0).items()).stream().map(ItemEntry::getItem).toList());
                }
            }
        }
        return new EmptySlotPayload(slot, ItemListing.of(items));
    }

    public void processRestock() {
        List<ItemStack> items = InventoryUtil.getNonEquipmentItems(Minecraft.getInstance().player.getInventory());
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            int space = item.getMaxStackSize() - item.count();
            if (space > 0) {
                ClientNetworkUtil.sendPacket(new RefillItemPayload(i));
            }
        }
    }

    public Component getDisplayname(ItemStack item) {
        if (dev.tr7zw.transition.mc.ItemUtil.hasCustomName(item)) {
            return item.getHoverName().copy();
        }
        NameProvider provider = providerManager.getNameProvider(item);
        if (provider != null) {
            return provider.getDisplayName(item).copy();
        }
        return item.getHoverName().copy();
    }

    public Component getDisplayname(ItemEntry entry) {
        if (entry == null) {
            return null;
        }
        if (entry.getNameOverwride() != null) {
            return entry.getNameOverwride();
        }
        return getDisplayname(entry.getItem().getDefaultInstance());
    }

}

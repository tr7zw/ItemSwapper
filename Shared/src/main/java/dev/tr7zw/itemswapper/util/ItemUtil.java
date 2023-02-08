package dev.tr7zw.itemswapper.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemUtil {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    private static final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    
    private ItemUtil() {
        // private
    }

    public static int inventorySlotToHudSlot(int slot) {
        if (slot < 9) {
            return 36 + slot;
        }
        return slot;
    }

    public static boolean inArray(Item[] items, Item item) {
        for (Item i : items) {
            if (i == item) {
                return true;
            }
        }
        return false;
    }

    public static ItemEntry[] toDefault(Item[] items) {
        ItemEntry[] entries = new ItemEntry[items.length];
        for (int i = 0; i < items.length; i++) {
            entries[i] = new ItemEntry(items[i], null);
        }
        return entries;
    }

    @NotNull
    public static Item[] itemstackToSingleItem(Item[] items) {
        int lastItem = 0;
        for (int x = 0; x < items.length; x++) {
            if (items[x] != Items.AIR) {
                lastItem = x;
            }
        }
        items = Arrays.copyOf(items, lastItem + 1);
        return items;
    }

    public static Component getDisplayname(ItemStack item) {
        if (item.hasCustomHoverName()) {
            return item.getHoverName();
        }
        NameProvider provider = ItemSwapperSharedMod.instance.getClientProviderManager()
                .getNameProvider(item.getItem());
        if (provider != null) {
            return provider.getDisplayName(item);
        }
        return item.getHoverName();
    }
    
    public static boolean grabItem(Item item, boolean ignoreHotbar) {
        List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(item, true, ignoreHotbar);
        if (!slots.isEmpty()) {
            AvailableSlot slot = slots.get(0);
            OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
            if (event.canceled().get()) {
                // interaction canceled by some other mod
                return false;
            }
            if (slot.inventory() == -1) {
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                        hudSlot, minecraft.player.getInventory().selected,
                        ClickType.SWAP, minecraft.player);
            } else {
                NetworkUtil.swapItem(slot.inventory(), slot.slot());
            }
            clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
            return true;
        }
        return false;
    }

}

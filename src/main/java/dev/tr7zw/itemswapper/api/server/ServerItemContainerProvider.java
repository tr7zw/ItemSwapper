package dev.tr7zw.itemswapper.api.server;

import dev.tr7zw.itemswapper.api.*;
import dev.tr7zw.itemswapper.packets.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;

public interface ServerItemContainerProvider {

    /**
     * @return A list of all Items this provider wants to process
     */
    Set<Item> getItemHandlers();

    /**
     * @param itemStack The itemstack to process
     * @param item      The target item to find
     * @param limit     if true, bestcase only a single slot should be returned, the
     *                  rest will be ignored
     * @return A list of {@link AvailableSlot}s that can be swapped to right now.
     *         Return an {@link Collections#emptyList() emptyList} when nothing is
     *         found/the itemstack can't be used.
     */
    List<RemoteItem> processItemStack(ServerPlayer player, ItemStack itemStack, Item item, boolean limit, int slotId);

    NonNullList<RemoteItem> getItemStacks(ServerPlayer player, ItemStack itemStack, int slotId);

    /**
     *
     * @param container The container itemstack, for example a shulker box
     * @param itemStack The itemstack to store, for example cobblestone
     * @return amount stored away, 0 if nothing was stored or the provider doesn't
     *         exist
     */
    int insertItem(ServerPlayer player, ItemStack container, ItemStack itemStack);

    String getId();

    ItemStack removeItem(ServerPlayer player, ItemStack container, RemoteItem remoteItem);

    int takeFromSlot(ServerPlayer player, ItemStack container, RemoteItem remoteItem, int toTake);
}

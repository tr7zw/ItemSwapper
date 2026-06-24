package dev.tr7zw.itemswapper.server.provider;

import dev.tr7zw.itemswapper.api.server.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.server.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;

public abstract class ListContainerProvider implements ServerItemContainerProvider {

    public abstract boolean canStoreinContainer(Item itemstack);

    public abstract int getMaxSlots(ItemStack container);

    protected abstract NonNullList<ItemStack> getContent(ItemStack container);

    protected abstract void setContent(ItemStack container, NonNullList<ItemStack> content);

    protected boolean isValidContainer(ServerPlayer player, ItemStack container) {
        return getItemHandlers().contains(container.getItem()) && container.count() == 1;
    }

    @Override
    public List<RemoteItem> processItemStack(ServerPlayer player, ItemStack container, Item item, boolean limit,
            int slotId) {
        if (!isValidContainer(player, container) || !canStoreinContainer(item)) {
            return Collections.emptyList();
        }
        List<ItemStack> containerItems = getContent(container);
        List<RemoteItem> slots = new ArrayList<>();
        if (containerItems != null) {
            for (int x = 0; x < containerItems.size(); x++) {
                if (containerItems.get(x).getItem() == item) {
                    slots.add(new RemoteItem(getId(), containerItems.get(x), slotId, x, containerItems.get(x).count()));
                    if (limit) {
                        return slots;
                    }
                }
            }
        }
        return slots;
    }

    @Override
    public NonNullList<RemoteItem> getItemStacks(ServerPlayer player, ItemStack container, int slotId) {
        if (!isValidContainer(player, container)) {
            return NonNullList.create();
        }
        List<ItemStack> containerItems = getContent(container);
        NonNullList<RemoteItem> slots = NonNullList.create();
        if (containerItems != null && !containerItems.isEmpty()) {
            for (int x = containerItems.size() - 1; x >= 0; x--) {
                slots.add(new RemoteItem(getId(), containerItems.get(x), slotId, x, containerItems.get(x).count()));
            }
        }
        if (slots.size() < getMaxSlots(container)) {
            for (int x = slots.size(); x < getMaxSlots(container); x++) {
                slots.add(new RemoteItem(getId(), ItemStack.EMPTY, slotId, x, 0));
            }
        }
        return slots;
    }

    @Override
    public int insertItem(ServerPlayer player, ItemStack container, ItemStack itemStack) {
        if (!isValidContainer(player, container) || !canStoreinContainer(itemStack.getItem())) {
            return 0;
        }
        NonNullList<ItemStack> containerItems = getContent(container);
        int inserted = 0;
        int toInsert = itemStack.count();
        if (containerItems != null) {
            // try to insert into existing stack first
            for (int i = 0; i < containerItems.size(); i++) {
                ItemStack targetStack = containerItems.get(i);
                if (ServerItemUtil.isSame(targetStack, itemStack)
                        && targetStack.count() < targetStack.getMaxStackSize()) {
                    int amountToStore = Math.min(toInsert, targetStack.getMaxStackSize() - targetStack.count());
                    targetStack.grow(amountToStore);
                    inserted += amountToStore;
                    toInsert -= amountToStore;
                    if (toInsert <= 0) {
                        break;
                    }
                }
            }
            // then try to insert into empty slots
            if (toInsert > 0) {
                for (int i = 0; i < containerItems.size(); i++) {
                    ItemStack targetStack = containerItems.get(i);
                    if (targetStack.isEmpty()) {
                        int amountToStore = Math.min(toInsert, itemStack.getMaxStackSize());
                        ItemStack clone = itemStack.copy();
                        clone.setCount(amountToStore);
                        containerItems.set(i, clone);
                        inserted += amountToStore;
                        toInsert -= amountToStore;
                        if (toInsert <= 0) {
                            break;
                        }
                    }
                }
            }
            // if there is unused space, take that into account as well
            if (toInsert > 0 && containerItems.size() < getMaxSlots(container)) {
                ItemStack clone = itemStack.copy();
                clone.setCount(toInsert);
                containerItems.add(clone);
                inserted += toInsert;
            }
            if (inserted > 0) {
                setContent(container, containerItems);
            }
        }
        return inserted;
    }

    @Override
    public ItemStack removeItem(ServerPlayer player, ItemStack container, RemoteItem remoteItem) {
        if (!isValidContainer(player, container)) {
            return null;
        }
        NonNullList<ItemStack> containerItems = getContent(container);
        if (containerItems != null) {
            PlayerSession session = ItemSwapperSharedServer.INSTANCE.getPlayerManager().getSession(player);
            ItemStack targetStack = containerItems.get(remoteItem.id());
            if (targetStack.getItem() == remoteItem.itemStack().getItem()) {
                ItemStack removed = targetStack.copy();
                boolean hasMore = false;
                for (int i = 0; i < containerItems.size(); i++) {
                    if (i != remoteItem.id() && containerItems.get(i).getItem() == targetStack.getItem()) {
                        hasMore = true;
                        break;
                    }
                }
                if (!hasMore && session.isKeepLastItem() && targetStack.getMaxStackSize() > 1
                        && targetStack.count() != 1) {
                    // trying to grab the last item you will get it, even with keep last item enabled
                    removed.setCount(targetStack.count() - 1);
                    targetStack.setCount(1);
                } else {
                    targetStack.setCount(0);
                }
                setContent(container, containerItems);
                return removed;
            }
        }
        return null;
    }

    @Override
    public int takeFromSlot(ServerPlayer player, ItemStack container, RemoteItem remoteItem, int toTake){
        if (!isValidContainer(player, container)) {
            return 0;
        }
        NonNullList<ItemStack> containerItems = getContent(container);
        if (containerItems != null) {
            ItemStack targetStack = containerItems.get(remoteItem.id());
            if (targetStack.getItem() == remoteItem.itemStack().getItem()) {
                int taken = Math.min(toTake, targetStack.count());
                targetStack.shrink(taken);
                setContent(container, containerItems);
                return taken;
            }
        }
        return 0;
    }
}

package dev.tr7zw.itemswapper.server.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.server.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ShulkerContainerProvider implements ServerItemContainerProvider {

    private final static int SLOTS_PER_SHULKER = 27;
    private static Set<Item> shulkers = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
            Items.RED_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX);

    @Override
    public Set<Item> getItemHandlers() {
        return shulkers;
    }

    @Override
    public List<RemoteItem> processItemStack(ItemStack itemStack, Item item, boolean limit, int slotId) {
        if (!ItemSwapperSharedMod.instance.getClientUiManager().areShulkersEnabled()) {
            return Collections.emptyList();
        }
        List<ItemStack> shulkerItems = ShulkerHelper.getItems(itemStack);
        List<RemoteItem> slots = new ArrayList<>();
        if (shulkerItems != null) {
            for (int x = 0; x < shulkerItems.size(); x++) {
                if (shulkerItems.get(x).getItem() == item) {
                    slots.add(new RemoteItem(getId(), shulkerItems.get(x), slotId, x, shulkerItems.get(x).count()));
                    if (limit) {
                        return slots;
                    }
                }
            }
        }
        return slots;
    }

    @Override
    public NonNullList<RemoteItem> getItemStacks(ItemStack itemStack, int slotId) {
        if (!ItemSwapperSharedMod.instance.getClientUiManager().areShulkersEnabled()) {
            return NonNullList.create();
        }
        List<ItemStack> shulkerItems = ShulkerHelper.getItems(itemStack);
        NonNullList<RemoteItem> slots = NonNullList.create();
        if (shulkerItems != null) {
            for (int x = 0; x < shulkerItems.size(); x++) {
                slots.add(new RemoteItem(getId(), shulkerItems.get(x), slotId, x, shulkerItems.get(x).count()));
            }
        }
        if(slots.size() < SLOTS_PER_SHULKER) {
            for(int x = slots.size(); x < SLOTS_PER_SHULKER; x++) {
                slots.add(new RemoteItem(getId(), ItemStack.EMPTY, slotId, x, 0));
            }
        }
        return slots;
    }

    @Override
    public int insertItem(ItemStack container, ItemStack itemStack) {
        NonNullList<ItemStack> shulkerItems = ShulkerHelper.getItems(container);
        int inserted = 0;
        int toInsert = itemStack.getCount();
        if (shulkerItems != null) {
            // try to insert into existing stack first
            for (int i = 0; i < shulkerItems.size(); i++) {
                ItemStack targetStack = shulkerItems.get(i);
                if (ServerItemUtil.isSame(targetStack, itemStack)
                        && targetStack.getCount() < targetStack.getMaxStackSize()) {
                    int amountToStore = Math.min(toInsert, targetStack.getMaxStackSize() - targetStack.getCount());
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
                for (int i = 0; i < shulkerItems.size(); i++) {
                    ItemStack targetStack = shulkerItems.get(i);
                    if (targetStack.isEmpty()) {
                        int amountToStore = Math.min(toInsert, itemStack.getMaxStackSize());
                        ItemStack clone = itemStack.copy();
                        clone.setCount(amountToStore);
                        shulkerItems.set(i, clone);
                        inserted += amountToStore;
                        toInsert -= amountToStore;
                        if (toInsert <= 0) {
                            break;
                        }
                    }
                }
            }
            // if there is unused space, take that into account as well
            if(toInsert > 0 && shulkerItems.size() < SLOTS_PER_SHULKER) {
                int slot = shulkerItems.size();
                ItemStack clone = itemStack.copy();
                clone.setCount(toInsert);
                shulkerItems.add(clone);
                inserted += toInsert;
            }
            if (inserted > 0) {
                ShulkerHelper.setItem(container, shulkerItems);
            }
        }
        return inserted;
    }

    @Override
    public String getId() {
        return "itemswapper:shulker";
    }

    @Override
    public ItemStack removeItem(ItemStack container, RemoteItem remoteItem) {
        NonNullList<ItemStack> shulkerItems = ShulkerHelper.getItems(container);
        if (shulkerItems != null) {
            ItemStack targetStack = shulkerItems.get(remoteItem.id());
            if (targetStack.getItem() == remoteItem.itemStack().getItem()) {
                ItemStack removed = targetStack.copy();
                targetStack.setCount(0);
                ShulkerHelper.setItem(container, shulkerItems);
                return removed;
            }
        }
        return null;
    }

}

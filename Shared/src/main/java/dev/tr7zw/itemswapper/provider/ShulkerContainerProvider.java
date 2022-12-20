package dev.tr7zw.itemswapper.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ShulkerContainerProvider implements ContainerProvider {

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
    public List<AvailableSlot> processItemStack(ItemStack itemStack, Item item, boolean limit, int slotid) {
        if(!ItemSwapperSharedMod.instance.areShulkersEnabled()) {
            return Collections.emptyList();
        }
        List<ItemStack> shulkerItems = ShulkerHelper.getItems(itemStack);
        List<AvailableSlot> slots = new ArrayList<>();
        if (shulkerItems != null) {
            for (int x = 0; x < shulkerItems.size(); x++) {
                if (!(shulkerItems.get(x)).isEmpty()
                        && shulkerItems.get(x).getItem() == item) {
                    slots.add(new AvailableSlot(slotid, x, shulkerItems.get(x)));
                    if(limit) {
                        return slots;
                    }
                }
            }
        }
        return slots;
    }

}

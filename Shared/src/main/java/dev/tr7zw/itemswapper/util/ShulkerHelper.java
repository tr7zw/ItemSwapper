package dev.tr7zw.itemswapper.util;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShulkerHelper {

    private static Set<Item> shulkers = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
            Items.RED_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX);
    
    public static NonNullList<ItemStack> getItems(ItemStack shulker) {
        if(!shulkers.contains(shulker.getItem())) {
            return null;
        }
        CompoundTag tag = BlockItem.getBlockEntityData(shulker);
        if (tag != null && tag.contains("Items", CompoundTag.TAG_LIST)) {
            NonNullList<ItemStack> items = NonNullList.withSize(3 * 9, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(BlockItem.getBlockEntityData(shulker), items);
            return items;
        }
        return null;
    }

    public static void setItem(ItemStack shulker, NonNullList<ItemStack> items) {
        CompoundTag tag = BlockItem.getBlockEntityData(shulker);
        CompoundTag rootTag = ContainerHelper.saveAllItems(tag != null ? tag : new CompoundTag(), items);
        BlockItem.setBlockEntityData(shulker, BlockEntityType.SHULKER_BOX, rootTag);
    }
    
    public static boolean isShulker(Item item) {
        return shulkers.contains(item);
    }

}

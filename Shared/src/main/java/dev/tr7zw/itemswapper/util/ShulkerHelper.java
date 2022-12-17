package dev.tr7zw.itemswapper.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShulkerHelper {

    public static NonNullList<ItemStack> getItems(ItemStack shulker) {
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

}

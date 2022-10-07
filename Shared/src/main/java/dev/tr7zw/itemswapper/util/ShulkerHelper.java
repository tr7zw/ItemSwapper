package dev.tr7zw.itemswapper.util;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class ShulkerHelper {

    public static List<ItemStack> getItems(ItemStack shulker){
        CompoundTag tag = BlockItem.getBlockEntityData(shulker);
        if(tag != null && tag.contains("Items", CompoundTag.TAG_LIST)) {
            NonNullList<ItemStack> items = NonNullList.withSize(3*9, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(BlockItem.getBlockEntityData(shulker), items);
            return items;
        }
        return null;
    }
    
}

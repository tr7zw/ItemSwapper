package dev.tr7zw.itemswapper.util;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShulkerHelper {

    private ShulkerHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static List<ItemStack> getItems(ItemStack shulker) {
        CompoundTag tag = BlockItem.getBlockEntityData(shulker);

        if (tag != null && tag.contains("Items", Tag.TAG_LIST)) {
            NonNullList<ItemStack> items = NonNullList.withSize(3 * 9, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items);
            return items;
        }

        return Collections.emptyList();
    }

    public static void setItem(ItemStack shulker, NonNullList<ItemStack> items) {
        CompoundTag tag = BlockItem.getBlockEntityData(shulker);
        CompoundTag rootTag = ContainerHelper.saveAllItems(tag != null ? tag : new CompoundTag(), items);
        BlockItem.setBlockEntityData(shulker, BlockEntityType.SHULKER_BOX, rootTag);
    }
}

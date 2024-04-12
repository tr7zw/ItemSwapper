package dev.tr7zw.itemswapper.util;

import net.minecraft.world.item.ItemStack;

public class NMSWrapper {

    public static boolean isSame(ItemStack a, ItemStack b) {
        // spotless:off 
        //#if MC <= 12004
        //$$ return ItemStack.isSameItemSameTags(a, b);
        //#else
        return ItemStack.isSameItemSameComponents(a, b);
        //#endif
       //spotless:on
    }

    public static boolean hasCustomName(ItemStack stack) {
        // spotless:off 
        //#if MC <= 12004
        //$$ return stack.hasCustomHoverName();
        //#else
        return stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
        //#endif
       //spotless:on
    }

}

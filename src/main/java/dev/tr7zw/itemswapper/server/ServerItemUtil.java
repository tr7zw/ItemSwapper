package dev.tr7zw.itemswapper.server;

import net.minecraft.world.item.*;

public class ServerItemUtil {

    public static boolean isSame(ItemStack a, ItemStack b) {
        //? if < 1.17.0 {

        // return ItemStack.isSame(a, b);
        //? } else if <= 1.20.4 {

        /*return ItemStack.isSameItemSameTags(a, b);
         *///? } else {

        return ItemStack.isSameItemSameComponents(a, b);
        //? }
    }

}

package dev.tr7zw.itemswapper.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Contains copies from NMSHelper, because NMSHelper pulls the Minecraft class,
 * which will crash on servers
 */
public class ServerUtil {

    public static boolean isSame(ItemStack a, ItemStack b) {
        //#if MC < 11700
        //$$return ItemStack.isSame(a, b);
        //#elseif MC <= 12004
        //$$ return ItemStack.isSameItemSameTags(a, b);
        //#else
        return ItemStack.isSameItemSameComponents(a, b);
        //#endif
    }

    public static ResourceLocation getResourceLocation(String namespace, String path) {
        //#if MC >= 12100
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }
    
}

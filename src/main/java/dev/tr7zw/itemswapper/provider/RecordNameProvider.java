package dev.tr7zw.itemswapper.provider;

import java.util.Set;

import dev.tr7zw.itemswapper.accessor.ItemVariantAccess;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecordNameProvider implements NameProvider {

    @Override
    public Set<Item> getItemHandlers() {
        // spotless:off
        //#if MC >= 12100
        return ((ItemVariantAccess) (Object) new Items()).getAllItemVariants();
        //#else
        //$$ return ((ItemVariantAccess) (Object) Items.MUSIC_DISC_CAT).getAllItemVariants();
        //#endif
        //spotless:on
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        return ComponentProvider.translatable(item.getItem().getDescriptionId() + ".desc");
    }

}

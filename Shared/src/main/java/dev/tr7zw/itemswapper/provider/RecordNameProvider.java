package dev.tr7zw.itemswapper.provider;

import java.util.Set;

import dev.tr7zw.itemswapper.accessor.RecordItemAccess;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RecordNameProvider implements NameProvider {

    @Override
    public Set<Item> getItemHandlers() {
        // TODO late init this, so other mods music discs are loaded?
        return ((RecordItemAccess) (Object) Items.MUSIC_DISC_CAT).getAllRecords();
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        return Component.translatable(item.getItem().getDescriptionId() + ".desc");
    }

}

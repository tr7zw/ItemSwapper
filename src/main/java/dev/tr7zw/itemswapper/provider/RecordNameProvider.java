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
        // TODO late init this, so other mods music discs are loaded?
        // spotless:off
        //#if MC >= 12100
            return Set.of(
                    Items.MUSIC_DISC_13
                    ,Items.MUSIC_DISC_CAT
                    ,Items.MUSIC_DISC_BLOCKS
                    ,Items.MUSIC_DISC_CHIRP
                    ,Items.MUSIC_DISC_CREATOR
                    ,Items.MUSIC_DISC_CREATOR_MUSIC_BOX
                    ,Items.MUSIC_DISC_FAR
                    ,Items.MUSIC_DISC_MALL
                    ,Items.MUSIC_DISC_MELLOHI
                    ,Items.MUSIC_DISC_STAL
                    ,Items.MUSIC_DISC_STRAD
                    ,Items.MUSIC_DISC_WARD
                    ,Items.MUSIC_DISC_11
                    ,Items.MUSIC_DISC_WAIT
                    ,Items.MUSIC_DISC_OTHERSIDE
                    ,Items.MUSIC_DISC_RELIC
                    ,Items.MUSIC_DISC_5
                    ,Items.MUSIC_DISC_PIGSTEP
                    ,Items.MUSIC_DISC_PRECIPICE);
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

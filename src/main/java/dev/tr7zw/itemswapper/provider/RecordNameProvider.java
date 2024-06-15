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

    private Set<Item> BY_NAME;

    @Override
    public Set<Item> getItemHandlers() {
        // TODO late init this, so other mods music discs are loaded?
        //spotless:off
        //#if MC >= 12100
            BY_NAME.add(Items.MUSIC_DISC_13);
            BY_NAME.add(Items.MUSIC_DISC_CAT);
            BY_NAME.add(Items.MUSIC_DISC_BLOCKS);
            BY_NAME.add(Items.MUSIC_DISC_CHIRP);
            BY_NAME.add(Items.MUSIC_DISC_CREATOR);
            BY_NAME.add(Items.MUSIC_DISC_CREATOR_MUSIC_BOX);
            BY_NAME.add(Items.MUSIC_DISC_FAR);
            BY_NAME.add(Items.MUSIC_DISC_MALL);
            BY_NAME.add(Items.MUSIC_DISC_MELLOHI);
            BY_NAME.add(Items.MUSIC_DISC_STAL);
            BY_NAME.add(Items.MUSIC_DISC_STRAD);
            BY_NAME.add(Items.MUSIC_DISC_WARD);
            BY_NAME.add(Items.MUSIC_DISC_11);
            BY_NAME.add(Items.MUSIC_DISC_WAIT);
            BY_NAME.add(Items.MUSIC_DISC_OTHERSIDE);
            BY_NAME.add(Items.MUSIC_DISC_RELIC);
            BY_NAME.add(Items.MUSIC_DISC_5);
            BY_NAME.add(Items.MUSIC_DISC_PIGSTEP);
            BY_NAME.add(Items.MUSIC_DISC_PRECIPICE);
            return BY_NAME;
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

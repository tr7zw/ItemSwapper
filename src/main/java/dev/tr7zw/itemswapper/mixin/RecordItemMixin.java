package dev.tr7zw.itemswapper.mixin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.itemswapper.accessor.ItemVariantAccess;

// hack, as records have been completely reworked, for modded support
// especially new mixin to intercept when jukeboxPlayable() is called and
// registering this then would be needed

//spotless:off
//#if MC >= 12100
import net.minecraft.world.item.Items;

@Mixin(Items.class)
public class RecordItemMixin implements ItemVariantAccess {
    private Set<Item> BY_NAME;

    @Override
    public Set<Item> getAllItemVariants() {
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
    }
}

//#else
//$$ import net.minecraft.sounds.SoundEvent;
//$$ import net.minecraft.world.item.Item;
//$$ import net.minecraft.world.item.RecordItem;
//$$
//$$ @Mixin(RecordItem.class)
//$$ public class RecordItemMixin implements ItemVariantAccess {
//$$
//$$     @Shadow
//$$     private static Map<SoundEvent, RecordItem> BY_NAME;
//$$
//$$     @Override
//$$     public Set<Item> getAllItemVariants() {
//$$         return new HashSet<>(BY_NAME.values());
//$$     }
//$$
//$$ }
//#endif
//spotless:on
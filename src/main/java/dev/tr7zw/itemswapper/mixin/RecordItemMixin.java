package dev.tr7zw.itemswapper.mixin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.itemswapper.accessor.ItemVariantAccess;

import net.minecraft.world.item.Item;

// hack, as records have been completely reworked, for modded support
// especially new mixin to intercept when jukeboxPlayable() is called and
// registering this then would be needed

//spotless:off
//#if MC >= 12100
import net.minecraft.world.item.Items;

@Mixin(Items.class)
public class RecordItemMixin implements ItemVariantAccess {

    @Override
    public Set<Item> getAllItemVariants() {
        Set<Item> BY_NAME = Set.of(
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
        return BY_NAME;
    }
}

//#else
//$$ import net.minecraft.sounds.SoundEvent;
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
package dev.tr7zw.itemswapper.mixin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.component.DataComponents;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public class RecordItemMixin implements ItemVariantAccess {
    private static Set<Item> BY_NAME = new HashSet<>();

    @Inject(method = "registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item;", at = @At("HEAD"))
    private static void registerItemMixin(String key, Item item, CallbackInfoReturnable<Item> cir) {
        if (BY_NAME == null) { BY_NAME = new HashSet<>(); }
        if (item.components().has(DataComponents.JUKEBOX_PLAYABLE)) {
            BY_NAME.add(item);
        };
    }

    @Override
    public Set<Item> getAllItemVariants() {
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
package dev.tr7zw.itemswapper.mixin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.itemswapper.accessor.RecordItemAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

@Mixin(RecordItem.class)
public class RecordItemMixin implements RecordItemAccess {

    @Shadow
    private static Map<SoundEvent, RecordItem> BY_NAME;

    @Override
    public Set<Item> getAllRecords() {
        return new HashSet<>(BY_NAME.values());
    }

}

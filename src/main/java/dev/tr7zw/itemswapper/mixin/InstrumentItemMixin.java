package dev.tr7zw.itemswapper.mixin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.itemswapper.accessor.InstrumentItemAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;

import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//spotless:off
//#if MC >=12102
import net.minecraft.core.HolderLookup.Provider;
//#else
//$$ import net.minecraft.tags.TagKey;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif
//spotless:on

@Mixin(InstrumentItem.class)
public abstract class InstrumentItemMixin implements InstrumentItemAccess {

    private Set<Item> items = new HashSet<>();

// spotless:off
//#if MC < 12102
//$$    @Inject(method = "<init>", at = @At("RETURN"))
//$$    public void constructor(Item.Properties properties, TagKey<Instrument> tagKey, CallbackInfo ci) {
//$$        items.add((Item) (Object) this);
//$$    }
//#endif
//spotless:on

    @Override
    public Set<Item> getItems() {
        return items;
    }

    // spotless:off
    //#if MC >= 12102
    @Override
    public Optional<? extends Holder<Instrument>> getOptionalInstrument(ItemStack itemStack) {
        return getInstrument(itemStack, Minecraft.getInstance().level.registryAccess());
    }
    
    @Shadow
    protected abstract Optional<Holder<Instrument>> getInstrument(ItemStack itemStack, Provider provider);
    //#else
    //$$@Override
    //$$public Optional<? extends Holder<Instrument>> getOptionalInstrument(ItemStack itemStack) {
    //$$    return getInstrument(itemStack);
    //$$}
    //$$    @Shadow
    //$$ protected abstract Optional<? extends Holder<Instrument>> getInstrument(ItemStack itemStack);
    //#endif
    //spotless:on

}

package dev.tr7zw.itemswapper.accessor;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface InstrumentItemAccess {

    public Set<Item> getItems();

    public Optional<? extends Holder<Instrument>> getOptionalInstrument(ItemStack itemStack);

}

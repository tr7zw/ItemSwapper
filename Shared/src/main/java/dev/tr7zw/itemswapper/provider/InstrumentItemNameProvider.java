package dev.tr7zw.itemswapper.provider;

import java.util.Optional;
import java.util.Set;

import dev.tr7zw.itemswapper.accessor.InstrumentItemAccess;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InstrumentItemNameProvider implements NameProvider {

    @Override
    public Set<Item> getItemHandlers() {
        return ((InstrumentItemAccess) (Object) Items.GOAT_HORN).getItems();
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        Optional<ResourceKey<Instrument>> optional = ((InstrumentItemAccess) (Object) item.getItem())
                .getOptionalInstrument(item)
                .flatMap(Holder::unwrapKey);
        if (optional.isPresent()) {
            return Component
                    .translatable(Util.makeDescriptionId("instrument", optional.get().location()));
        }
        return item.getHoverName();
    }

}

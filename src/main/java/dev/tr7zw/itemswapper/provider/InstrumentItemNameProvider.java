package dev.tr7zw.itemswapper.provider;

import java.util.Optional;

import dev.tr7zw.itemswapper.accessor.InstrumentItemAccess;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.*;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;

public class InstrumentItemNameProvider implements NameProvider {

    @Override
    public boolean isProvider(ItemStack item) {
        return item.getItem() instanceof InstrumentItemAccess;
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        Optional<ResourceKey<Instrument>> optional = ((InstrumentItemAccess) (Object) item.getItem())
                .getOptionalInstrument(item).flatMap(Holder::unwrapKey);
        if (optional.isPresent()) {
            return Component.translatable(Util.makeDescriptionId("instrument",
                    optional.get()/*? >= 1.21.11 {*/ .identifier() /*?} else {*//* .location() *//*?}*/));
        }
        return item.getHoverName();
    }

}

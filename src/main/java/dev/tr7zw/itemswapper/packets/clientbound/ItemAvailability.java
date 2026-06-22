package dev.tr7zw.itemswapper.packets.clientbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

import java.util.*;

public record ItemAvailability(List<RemoteItem> items) implements CustomPacketPayloadSupport {

    public static final ItemAvailability INSTANCE = new ItemAvailability(Collections.emptyList());
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "item_availability").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        RemoteItem.writeList(paramFriendlyByteBuf, items);
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new ItemAvailability(friendlyByteBuf);
    }

    public ItemAvailability(FriendlyByteBuf buffer) {
        this(RemoteItem.parseList(buffer));
    }

}

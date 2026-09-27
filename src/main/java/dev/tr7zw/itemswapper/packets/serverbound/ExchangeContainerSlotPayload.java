package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

public record ExchangeContainerSlotPayload(int selectedSlot, RemoteItem remoteItem)
        implements CustomPacketPayloadSupport {

    public static final ExchangeContainerSlotPayload INSTANCE = new ExchangeContainerSlotPayload(0, (RemoteItem) null);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "exchange_container_slot").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(selectedSlot);
        remoteItem.write(paramFriendlyByteBuf);
    }

    @Override
    public ExchangeContainerSlotPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new ExchangeContainerSlotPayload(friendlyByteBuf);
    }

    public ExchangeContainerSlotPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), RemoteItem.parse(buffer));
    }

}

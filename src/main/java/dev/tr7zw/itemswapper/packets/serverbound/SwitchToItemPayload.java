package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

public record SwitchToItemPayload(int inventorySlot, RemoteItem remoteItem) implements CustomPacketPayloadSupport {

    public static final SwitchToItemPayload INSTANCE = new SwitchToItemPayload(0, null);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "switch_item").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(inventorySlot);
        remoteItem.write(paramFriendlyByteBuf);
    }

    @Override
    public SwitchToItemPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new SwitchToItemPayload(friendlyByteBuf);
    }

    public SwitchToItemPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), RemoteItem.parse(buffer));
    }

}

package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

public record RequestContainerPayload(int requestId, int slot) implements CustomPacketPayloadSupport {

    public static final RequestContainerPayload INSTANCE = new RequestContainerPayload(0, 0);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "request_container").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(requestId);
        paramFriendlyByteBuf.writeInt(slot);
    }

    @Override
    public RequestContainerPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new RequestContainerPayload(friendlyByteBuf);
    }

    public RequestContainerPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

}

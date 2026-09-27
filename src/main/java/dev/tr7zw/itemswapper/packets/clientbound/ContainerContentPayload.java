package dev.tr7zw.itemswapper.packets.clientbound;

import java.util.*;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

public record ContainerContentPayload(int requestId, int slot, boolean container, List<RemoteItem> items)
        implements CustomPacketPayloadSupport {

    public static final ContainerContentPayload INSTANCE = new ContainerContentPayload(0, 0, false, List.of());
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "container_content").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(requestId);
        paramFriendlyByteBuf.writeInt(slot);
        paramFriendlyByteBuf.writeBoolean(container);
        RemoteItem.writeList(paramFriendlyByteBuf, items);
    }

    @Override
    public ContainerContentPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new ContainerContentPayload(friendlyByteBuf);
    }

    public ContainerContentPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt(), buffer.readBoolean(), RemoteItem.parseList(buffer));
    }

}

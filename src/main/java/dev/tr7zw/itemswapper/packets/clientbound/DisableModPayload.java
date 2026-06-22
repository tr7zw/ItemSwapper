package dev.tr7zw.itemswapper.packets.clientbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

/**
 * Packet to enable/disable the mod on the client. This is used to prevent the
 * mod from being used on servers that do not allow it.
 * 
 * @param enabled
 */
public record DisableModPayload(boolean enabled) implements CustomPacketPayloadSupport {

    public static final DisableModPayload INSTANCE = new DisableModPayload(false);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "disable").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeBoolean(enabled);
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new DisableModPayload(friendlyByteBuf);
    }

    public DisableModPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}

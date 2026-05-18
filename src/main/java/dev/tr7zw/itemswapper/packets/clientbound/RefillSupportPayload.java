package dev.tr7zw.itemswapper.packets.clientbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

/**
 * Packet to enable/disable refill support on the client.
 * 
 * @param enabled
 */
@Deprecated
public record RefillSupportPayload(boolean enabled) implements CustomPacketPayloadSupport {

    public static final RefillSupportPayload INSTANCE = new RefillSupportPayload(true);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "enablerefill").id();

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
        return new RefillSupportPayload(friendlyByteBuf);
    }

    public RefillSupportPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}

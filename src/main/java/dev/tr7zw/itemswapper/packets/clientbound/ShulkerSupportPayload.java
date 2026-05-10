package dev.tr7zw.itemswapper.packets.clientbound;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.*;

/**
 * Packet to enable/disable shulker support on the client.
 * 
 * @param enabled
 */
@Deprecated
public record ShulkerSupportPayload(boolean enabled) implements CustomPacketPayloadSupport {

    public static final ShulkerSupportPayload INSTANCE = new ShulkerSupportPayload(false);
    public static final Identifier ID = McId.create(ItemSwapperMod.MODID, "enableshulker").id();

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
        return new ShulkerSupportPayload(friendlyByteBuf);
    }

    public ShulkerSupportPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}

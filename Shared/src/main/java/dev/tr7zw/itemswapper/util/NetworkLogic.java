package dev.tr7zw.itemswapper.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkLogic {

    public final static ResourceLocation enableMessage = new ResourceLocation("itemswapper", "enabled");
    
    public static void sendServerSupportPacket(ServerPlayer player) {
        player.connection.send(new ClientboundCustomPayloadPacket(enableMessage, new FriendlyByteBuf(Unpooled.copyBoolean(true))));
    }
    
}

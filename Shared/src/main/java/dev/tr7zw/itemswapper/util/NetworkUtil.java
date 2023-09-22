package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkUtil {

    public static final ResourceLocation enableShulkerMessage = new ResourceLocation(ItemSwapperMod.MODID,
            "enableshulker");
    public static final ResourceLocation enableRefillMessage = new ResourceLocation(ItemSwapperMod.MODID,
            "enablerefill");
    public static final ResourceLocation disableModMessage = new ResourceLocation(ItemSwapperMod.MODID, "disable");
    public static final ResourceLocation swapMessage = new ResourceLocation(ItemSwapperMod.MODID, "swap");
    public static final ResourceLocation refillMessage = new ResourceLocation(ItemSwapperMod.MODID, "refill");

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendShulkerSupportPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new PacketByteBufPayload(enableShulkerMessage,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled)))));
    }
    
    public static void sendRefillSupportPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new PacketByteBufPayload(enableRefillMessage,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled)))));
    }

    public static void sendDisableModPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new PacketByteBufPayload(disableModMessage,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled)))));
    }

    public static void swapItem(int inventorySlot, int slot) {
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeInt(inventorySlot);
        buf.writeInt(slot);
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new PacketByteBufPayload(swapMessage, new FriendlyByteBuf(buf))));
    }
    
    public static void refillItem(int targetSlot) {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeInt(targetSlot);
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new PacketByteBufPayload(refillMessage, new FriendlyByteBuf(buf))));
    }
    
}

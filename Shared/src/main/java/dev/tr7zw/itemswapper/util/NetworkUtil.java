package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkUtil {

    public static final ResourceLocation enableShulkerMessage = new ResourceLocation(ItemSwapperMod.MODID, "enableshulker");
    public static final ResourceLocation disableModMessage = new ResourceLocation(ItemSwapperMod.MODID, "disable");
    public static final ResourceLocation swapMessage = new ResourceLocation(ItemSwapperMod.MODID, "swap");

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendServerSupportPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(enableShulkerMessage,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
    }

    public static void sendDisableModPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(disableModMessage,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
    }

    public static void swapItem(int inventorySlot, int slot) {
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeInt(inventorySlot);
        buf.writeInt(slot);
        Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(swapMessage, new FriendlyByteBuf(buf)));
    }
}

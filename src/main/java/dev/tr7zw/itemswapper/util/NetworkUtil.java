package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;

import net.minecraft.client.Minecraft;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;

// spotless:off 
//#if MC <= 12001
//$$ import io.netty.buffer.ByteBuf;
//$$ import io.netty.buffer.Unpooled;
//$$ import net.minecraft.network.FriendlyByteBuf;
//#endif
//spotless:on

public class NetworkUtil {

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendShulkerSupportPacket(ServerPlayer player, boolean enabled) {
        // spotless:off 
        //#if MC >= 12002
        player.connection.send(new ClientboundCustomPayloadPacket(new ShulkerSupportPayload(enabled)));
        //#else
        //$$ player.connection.send(new ClientboundCustomPayloadPacket(ShulkerSupportPayload.ID,
        //$$        new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        //#endif
        //spotless:on
    }

    public static void sendRefillSupportPacket(ServerPlayer player, boolean enabled) {
        // spotless:off 
        //#if MC >= 12002
        player.connection.send(new ClientboundCustomPayloadPacket(new RefillSupportPayload(enabled)));
        //#else
        //$$ player.connection.send(new ClientboundCustomPayloadPacket(RefillSupportPayload.ID,
        //$$ new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        //#endif
        //spotless:on
    }

    public static void sendDisableModPacket(ServerPlayer player, boolean enabled) {
        // spotless:off 
        //#if MC >= 12002
        player.connection.send(new ClientboundCustomPayloadPacket(new DisableModPayload(enabled)));
        //#else
        //$$         player.connection.send(new ClientboundCustomPayloadPacket(DisableModPayload.ID,
        //$$ new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        //#endif
        //spotless:on
    }

    public static void swapItem(int inventorySlot, int slot) {
        // spotless:off 
        //#if MC >= 12002
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new SwapItemPayload(inventorySlot, slot)));
        //#else
          //$$ ByteBuf buf = Unpooled.buffer(8);
          //$$ buf.writeInt(inventorySlot);
          //$$ buf.writeInt(slot);
          //$$ Minecraft.getInstance().getConnection()
          //$$         .send(new ServerboundCustomPayloadPacket(SwapItemPayload.ID, new FriendlyByteBuf(buf)));
        //#endif
        //spotless:on
    }

    public static void refillItem(int targetSlot) {
        // spotless:off 
        //#if MC >= 12002
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new RefillItemPayload(targetSlot)));
        //#else
          //$$ ByteBuf buf = Unpooled.buffer(4);
          //$$ buf.writeInt(targetSlot);
          //$$ Minecraft.getInstance().getConnection()
          //$$         .send(new ServerboundCustomPayloadPacket(RefillItemPayload.ID, new FriendlyByteBuf(buf)));
      //#endif
      //spotless:on
    }

}

package dev.tr7zw.itemswapper.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

// spotless:off
//#if MC >= 12002
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//#endif
//#if MC <= 12001
//$$ import io.netty.buffer.ByteBuf;
//$$ import io.netty.buffer.Unpooled;
//$$ import net.minecraft.network.FriendlyByteBuf;
//#endif
//#if MC >= 12005
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
//#else
//$$ import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
//$$ import net.minecraft.server.network.ServerGamePacketListenerImpl;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketSender;
//$$ import net.fabricmc.fabric.api.networking.v1.PacketSender;
//$$ import net.minecraft.client.multiplayer.ClientPacketListener;
//#endif
//spotless:on

public class NetworkUtil {

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
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

    public static <T extends CustomPacketPayload> void registerClientCustomPacket(Class<T> type, ResourceLocation id,
            Function<FriendlyByteBuf, T> streamMemberEncoder, BiConsumer<T, FriendlyByteBuf> streamDecoder,
            Consumer<T> action) {
        // spotless:off 
        //#if MC > 12005
        if(PayloadTypeRegistryImpl.PLAY_S2C.get(id) == null) {
            PayloadTypeRegistryImpl.PLAY_S2C.register(new Type<>(id), new StreamCodec<FriendlyByteBuf, T>() {
    
                @Override
                public T decode(FriendlyByteBuf buffer) {
                    return streamMemberEncoder.apply(buffer);
                }
    
                @Override
                public void encode(FriendlyByteBuf buffer, T object) {
                    streamDecoder.accept(object, buffer);
                }
    
            });
        }
        ClientPlayNetworking.registerReceiver(new Type<T>(id), new ClientPlayNetworking.PlayPayloadHandler<T>() {

            @Override
            public void receive(T payload,
                    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context) {
                action.accept(payload);
                
            }
        });
        //#else
      //$$  ClientPlayNetworking.registerGlobalReceiver(id, new net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler() {
          //$$
          //$$      @Override
          //$$     public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf,
                  //$$              PacketSender responseSender) {
              //$$        action.accept(streamMemberEncoder.apply(buf));
             //$$     }
      //$$  });
        //#endif
        //spotless:on
    }

}

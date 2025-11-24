package dev.tr7zw.itemswapper.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.resources.*;

//? if >= 1.20.2 {

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? } else {
/*
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
*///? }
   //? if <= 1.20.1 {
   /*
   import io.netty.buffer.ByteBuf;
   import io.netty.buffer.Unpooled;
   import net.minecraft.network.FriendlyByteBuf;
   *///? }
   //? if >= 1.20.5 {

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
//? } else {
/*
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.multiplayer.ClientPacketListener;
*///? }

public class NetworkUtil {

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void swapItem(int inventorySlot, int slot) {
        //? if >= 1.20.2 {

        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new SwapItemPayload(inventorySlot, slot)));
        //? } else {
        /*
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeInt(inventorySlot);
        buf.writeInt(slot);
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(SwapItemPayload.ID, new FriendlyByteBuf(buf)));
        *///? }
    }

    public static void refillItem(int targetSlot) {
        //? if >= 1.20.2 {

        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new RefillItemPayload(targetSlot)));
        //? } else {
        /*
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeInt(targetSlot);
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(RefillItemPayload.ID, new FriendlyByteBuf(buf)));
        *///? }
    }

    public static <T extends CustomPacketPayload> void registerServerCustomPacket(Class<T> type,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id,
            Function<FriendlyByteBuf, T> streamMemberEncoder, BiConsumer<T, FriendlyByteBuf> streamDecoder) {
        //? if > 1.20.5 {

        if (PayloadTypeRegistryImpl.PLAY_C2S.get(id) == null) {
            PayloadTypeRegistryImpl.PLAY_C2S.register(new Type<>(id), new StreamCodec<FriendlyByteBuf, T>() {

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
        //? }
    }

    public static <T extends CustomPacketPayload> void registerClientCustomPacket(Class<T> type,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id,
            Function<FriendlyByteBuf, T> streamMemberEncoder, BiConsumer<T, FriendlyByteBuf> streamDecoder,
            Consumer<T> action) {
        //? if > 1.20.5 {

        if (PayloadTypeRegistryImpl.PLAY_S2C.get(id) == null) {
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
        //? } else {
        /*
        ClientPlayNetworking.registerGlobalReceiver(id,
                new net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler() {
        
                    @Override
                    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf,
                            PacketSender responseSender) {
                        action.accept(streamMemberEncoder.apply(buf));
                    }
                });
        *///? }
    }

}

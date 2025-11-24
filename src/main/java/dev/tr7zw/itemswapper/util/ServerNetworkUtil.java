package dev.tr7zw.itemswapper.util;

import java.util.function.BiConsumer;
import java.util.function.Function;

import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;

//? if >= 1.20.5 {

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//? }
//? if >= 1.20.2 {

import net.minecraft.network.protocol.common.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? } else {
/*
import net.minecraft.network.protocol.game.*;
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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
//? } else {
/*
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
*///? }

public class ServerNetworkUtil {

    private ServerNetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendShulkerSupportPacket(ServerPlayer player, boolean enabled) {
        //? if >= 1.20.2 {

        player.connection.send(new ClientboundCustomPayloadPacket(new ShulkerSupportPayload(enabled)));
        //? } else {
        /*
        player.connection.send(new ClientboundCustomPayloadPacket(ShulkerSupportPayload.ID,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        *///? }
    }

    public static void sendRefillSupportPacket(ServerPlayer player, boolean enabled) {
        //? if >= 1.20.2 {

        player.connection.send(new ClientboundCustomPayloadPacket(new RefillSupportPayload(enabled)));
        //? } else {
        /*
        player.connection.send(new ClientboundCustomPayloadPacket(RefillSupportPayload.ID,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        *///? }
    }

    public static void sendDisableModPacket(ServerPlayer player, boolean enabled) {
        //? if >= 1.20.2 {

        player.connection.send(new ClientboundCustomPayloadPacket(new DisableModPayload(enabled)));
        //? } else {
        /*
        player.connection.send(new ClientboundCustomPayloadPacket(DisableModPayload.ID,
                new FriendlyByteBuf(Unpooled.copyBoolean(enabled))));
        *///? }
    }

    public static <T extends CustomPacketPayload> void registerClientCustomPacket(Class<T> type,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id,
            Function<FriendlyByteBuf, T> streamMemberEncoder, BiConsumer<T, FriendlyByteBuf> streamDecoder) {
        //? if > 1.20.5 {

        if (PayloadTypeRegistryImpl.PLAY_S2C.get(id) == null) {
            PayloadTypeRegistry.playS2C().register(new Type<>(id), new StreamCodec<FriendlyByteBuf, T>() {

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

    public static <T extends CustomPacketPayload> void registerServerCustomPacket(Class<T> type,
            /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id,
            Function<FriendlyByteBuf, T> streamMemberEncoder, BiConsumer<T, FriendlyByteBuf> streamDecoder,
            BiConsumer<T, ServerPlayer> action) {
        //? if > 1.20.5 {

        if (PayloadTypeRegistryImpl.PLAY_C2S.get(id) == null) {
            PayloadTypeRegistry.playC2S().register(new Type<>(id), new StreamCodec<FriendlyByteBuf, T>() {

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
        ServerPlayNetworking.registerGlobalReceiver(new Type<T>(id), new PlayPayloadHandler<T>() {

            @Override
            public void receive(T payload, Context context) {
                action.accept(payload, context.player());
            }
        });
        //? } else {
        /*
        ServerPlayNetworking.registerGlobalReceiver(id, new PlayChannelHandler() {
        
            @Override
            public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
                    FriendlyByteBuf buf, PacketSender responseSender) {
                action.accept(streamMemberEncoder.apply(buf), player);
            }
        });
        *///? }
    }

}

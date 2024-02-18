package dev.tr7zw.itemswapper.util;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkUtil {

    private NetworkUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendShulkerSupportPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new ShulkerSupportPayload(enabled)));
    }

    public static void sendRefillSupportPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new RefillSupportPayload(enabled)));
    }

    public static void sendDisableModPacket(ServerPlayer player, boolean enabled) {
        player.connection.send(new ClientboundCustomPayloadPacket(new DisableModPayload(enabled)));
    }

    public static void swapItem(int inventorySlot, int slot) {
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new SwapItemPayload(inventorySlot, slot)));
    }

    public static void refillItem(int targetSlot) {
        Minecraft.getInstance().getConnection()
                .send(new ServerboundCustomPayloadPacket(new RefillItemPayload(targetSlot)));
    }

}

package dev.tr7zw.itemswapper;

import dev.tr7zw.itemswapper.util.NetworkLogic;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ItemSwapperMod extends ItemSwapperSharedMod implements ClientModInitializer {

    @Override
    public void initModloader() {
        ClientTickEvents.START_CLIENT_TICK.register(e -> {
            this.clientTick();
        });
        KeyBindingHelper.registerKeyBinding(keybind);
        FabricLoader.getInstance().getModContainer("itemswapper")
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        new ResourceLocation("itemswapper", "default"),
                        container, ResourcePackActivationType.DEFAULT_ENABLED));
        ClientPlayConnectionEvents.INIT.register((handle, client) -> {
            ClientPlayNetworking.registerReceiver(NetworkLogic.enableShulkerMessage, new PlayChannelHandler() {
                
                @Override
                public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf,
                        PacketSender responseSender) {
                    try {
                        ItemSwapperSharedMod.instance.setEnableShulkers(buf.readBoolean());
                    }catch(Throwable th) {
                        ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                    }
                }
            });
            ClientPlayNetworking.registerReceiver(NetworkLogic.disableModMessage, new PlayChannelHandler() {
                
                @Override
                public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf,
                        PacketSender responseSender) {
                    try {
                        ItemSwapperSharedMod.instance.setModDisabled(buf.readBoolean());
                    }catch(Throwable th) {
                        ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                    }
                }
            });
        });

    }

    @Override
    public void onInitializeClient() {
        init();
    }

}
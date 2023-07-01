package dev.tr7zw.itemswapper;

import dev.tr7zw.itemswapper.compat.MidnightControllsSupport;
import dev.tr7zw.itemswapper.util.ViveCraftSupport;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ItemSwapperMod extends ItemSwapperSharedMod implements ClientModInitializer {

    @Override
    public void initModloader() {
        ClientTickEvents.START_CLIENT_TICK.register(event -> this.clientTick());
        KeyBindingHelper.registerKeyBinding(keybind);
        KeyBindingHelper.registerKeyBinding(openInventoryKeybind);

        // Register default resource pack
        FabricLoader.getInstance().getModContainer("itemswapper").ifPresent(
                container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        new ResourceLocation("itemswapper", "default"), container, Component.translatable("text.itemswapper.resourcepack.default"),
                        ResourcePackActivationType.DEFAULT_ENABLED));
        FabricLoader.getInstance().getModContainer("itemswapper").ifPresent(
                container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        new ResourceLocation("itemswapper", "classic"), container, Component.translatable("text.itemswapper.resourcepack.classic"),
                        ResourcePackActivationType.NORMAL));

        FabricLoader.getInstance().getModContainer("midnightcontrols").ifPresent(mod -> {
            ItemSwapperSharedMod.LOGGER.info("Adding MidnightControls support!");
            MidnightControlsCompat.HANDLERS.add(new MidnightControllsSupport());
        });

        FabricLoader.getInstance().getModContainer("vivecraft").ifPresent(mod -> {
            ItemSwapperSharedMod.LOGGER.info("Adding ViveCraft support...");

            Optional<ModContainer> midnightControls = FabricLoader.getInstance().getModContainer("midnightcontrols");
            // To handle case when VR players uses ViveCraft without VR Controllers
            if (midnightControls.isPresent()) {
                ItemSwapperSharedMod.LOGGER.info("ViveCraft support disabled due to MidnightControls being present!");
            } else {
                ViveCraftSupport.getInstance().init();
            }
        });

        ClientPlayConnectionEvents.INIT.register((handle, client) -> {
            ClientPlayNetworking.registerReceiver(NetworkUtil.enableShulkerMessage,
                    (client1, handler, buf, responseSender) -> {
                        try {
                            ItemSwapperSharedMod.instance.setEnableShulkers(buf.readBoolean());
                        } catch (Throwable th) {
                            ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                        }
                    });
            ClientPlayNetworking.registerReceiver(NetworkUtil.enableRefillMessage,
                    (client1, handler, buf, responseSender) -> {
                        try {
                            ItemSwapperSharedMod.instance.setEnableRefill(buf.readBoolean());
                        } catch (Throwable th) {
                            ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                        }
                    });
            ClientPlayNetworking.registerReceiver(NetworkUtil.disableModMessage,
                    (client12, handler, buf, responseSender) -> {
                        try {
                            ItemSwapperSharedMod.instance.setModDisabled(buf.readBoolean());
                        } catch (Throwable th) {
                            ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                        }
                    });
        });
    }

    @Override
    public void onInitializeClient() {
        init();
    }

}
package dev.tr7zw.itemswapper;

import java.util.Optional;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.compat.AmecsAPISupport;
import dev.tr7zw.itemswapper.compat.MidnightControllsSupport;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import dev.tr7zw.itemswapper.util.NetworkUtil;

import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.network.chat.Component;

public class ItemSwapperMod extends ItemSwapperSharedMod implements ClientModInitializer {

    @Override
    public void initModloader() {
        new ItemSwapperServerMod().onLoad();
        ClientTickEvents.START_CLIENT_TICK.register(event -> this.clientTick());
        KeyBindingHelper.registerKeyBinding(keybind);
        KeyBindingHelper.registerKeyBinding(openInventoryKeybind);

        // Register default resource pack

        FabricLoader.getInstance().getModContainer("itemswapper")
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        getResourceLocation("itemswapper", "default"), container,
                        Component.translatable("text.itemswapper.resourcepack.default"),
                        ResourcePackActivationType.DEFAULT_ENABLED));
        FabricLoader.getInstance().getModContainer("itemswapper")
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        getResourceLocation("itemswapper", "classic"), container,
                        Component.translatable("text.itemswapper.resourcepack.classic"),
                        ResourcePackActivationType.NORMAL));

        FabricLoader.getInstance().getModContainer("midnightcontrols").ifPresent(mod -> {
            ItemSwapperBase.LOGGER.info("Adding MidnightControls support!");
            MidnightControlsCompat.HANDLERS.add(new MidnightControllsSupport());
        });

        FabricLoader.getInstance().getModContainer("vivecraft").ifPresent(mod -> {
            ItemSwapperBase.LOGGER.info("Adding ViveCraft support...");
            Optional<ModContainer> midnightControls = FabricLoader.getInstance().getModContainer("midnightcontrols");
            // To handle case when VR players uses ViveCraft without VR Controllers
            if (midnightControls.isPresent()) {
                ItemSwapperBase.LOGGER.info("ViveCraft support disabled due to MidnightControls being present!");
            } else {
                ViveCraftSupport.getInstance().init();
            }
        });

        FabricLoader.getInstance().getModContainer("amecsapi").ifPresent(mod -> {
            ItemSwapperBase.LOGGER.info("Adding Amecs-API support!");
            AmecsAPISupport.getInstance().init();
        });

        SwapperResourceLoader.ResourceLoaderInit.init();

        ClientPlayConnectionEvents.INIT.register((handle, client) -> {
            NetworkUtil.registerServerCustomPacket(SwapItemPayload.class, SwapItemPayload.ID,
                    b -> new SwapItemPayload(b), (p, b) -> p.write(b));
            NetworkUtil.registerServerCustomPacket(RefillItemPayload.class, RefillItemPayload.ID,
                    b -> new RefillItemPayload(b), (p, b) -> p.write(b));

            NetworkUtil.registerClientCustomPacket(ShulkerSupportPayload.class, ShulkerSupportPayload.ID,
                    b -> new ShulkerSupportPayload(b), (p, b) -> p.write(b), payload -> {
                        try {
                            ItemSwapperSharedMod.instance.setEnableShulkers(payload.enabled());
                        } catch (Throwable th) {
                            ItemSwapperBase.LOGGER.error("Error while processing packet!", th);
                        }
                    });
            NetworkUtil.registerClientCustomPacket(RefillSupportPayload.class, RefillSupportPayload.ID,
                    b -> new RefillSupportPayload(b), (p, b) -> p.write(b), payload -> {
                        try {
                            ItemSwapperSharedMod.instance.setEnableRefill(payload.enabled());
                        } catch (Throwable th) {
                            ItemSwapperBase.LOGGER.error("Error while processing packet!", th);
                        }
                    });
            NetworkUtil.registerClientCustomPacket(DisableModPayload.class, DisableModPayload.ID,
                    b -> new DisableModPayload(b), (p, b) -> p.write(b), payload -> {
                        try {
                            ItemSwapperSharedMod.instance.setModDisabled(payload.enabled());
                        } catch (Throwable th) {
                            ItemSwapperBase.LOGGER.error("Error while processing packet!", th);
                        }
                    });
        });
    }

    @Override
    public void onInitializeClient() {
        init();
    }

}
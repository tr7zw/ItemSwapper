package dev.tr7zw.itemswapper;

import dev.tr7zw.itemswapper.compat.*;
import dev.tr7zw.itemswapper.manager.*;
import dev.tr7zw.itemswapper.overlay.*;
import dev.tr7zw.itemswapper.packets.clientbound.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.transition.loader.networking.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.loader.api.*;
import net.minecraft.client.*;
import net.minecraft.network.chat.*;

import java.util.*;

import static dev.tr7zw.transition.mc.GeneralUtil.*;

public class ItemSwapperMod extends ItemSwapperSharedMod implements ClientModInitializer {

    protected static final String ITEMSWAPPER = "itemswapper";

    @Override
    public void initModloader() {
        new ItemSwapperServerMod().onLoad();
        ClientTickEvents.START_CLIENT_TICK.register(event -> this.clientTick());
        clientUiManager.initModloader();

        // Register default resource pack

        FabricLoader.getInstance().getModContainer(ITEMSWAPPER)
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        getResourceLocation(ITEMSWAPPER, "default"), container,
                        Component.translatable("text.itemswapper.resourcepack.default"),
                        ResourcePackActivationType.DEFAULT_ENABLED));

        //? if < 26.0 {

        /*FabricLoader.getInstance().getModContainer("midnightcontrols").ifPresent(mod -> {
            ItemSwapperBase.LOGGER.info("Adding MidnightControls support!");
            eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat.HANDLERS
                    .add(new dev.tr7zw.itemswapper.compat.MidnightControllsSupport());
        });
        *///? }

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

        ClientNetworkUtil.registerPackets(handle -> {
            // Server packets
            handle.registerServerCustomPacket(SwapItemPayload.INSTANCE);
            handle.registerServerCustomPacket(RefillItemPayload.INSTANCE);
            handle.registerServerCustomPacket(RequestAvailability.INSTANCE);
            handle.registerServerCustomPacket(EmptySlotPayload.INSTANCE);
            handle.registerServerCustomPacket(SwitchToItemPayload.INSTANCE);
            // Client packets
            handle.registerClientCustomPacket(ShulkerSupportPayload.INSTANCE, payload -> {
                ItemSwapperSharedMod.instance.getSessionSettings().setEnableShulkers(payload.enabled());
            });
            handle.registerClientCustomPacket(RefillSupportPayload.INSTANCE, payload -> {
                ItemSwapperSharedMod.instance.getSessionSettings().setEnableRefill(payload.enabled());
            });
            handle.registerClientCustomPacket(DisableModPayload.INSTANCE, payload -> {
                ItemSwapperSharedMod.instance.getSessionSettings().setModDisabled(payload.enabled());
            });
            handle.registerClientCustomPacket(ItemAvailability.INSTANCE, payload -> {
                ItemSwapperSharedMod.instance.getSessionSettings().updateItemInfo(payload.items());
                if (Minecraft.getInstance().screen instanceof SwitchItemOverlay overlay) {
                    overlay.processRemoteUpdate();
                }
            });
        });

    }

    @Override
    public void onInitializeClient() {
        init();
    }

}

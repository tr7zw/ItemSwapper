package dev.tr7zw.itemswapper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
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
    }

    @Override
    public void onInitializeClient() {
        init();
    }

}
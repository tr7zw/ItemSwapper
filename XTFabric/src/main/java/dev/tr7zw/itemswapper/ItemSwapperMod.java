package dev.tr7zw.itemswapper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class ItemSwapperMod extends ItemSwapperSharedMod implements ClientModInitializer {
	
    @Override
    public void initModloader() {
        ClientTickEvents.START_CLIENT_TICK.register(e ->
        {
            this.clientTick();
        });
        KeyBindingHelper.registerKeyBinding(keybind);
    }

    @Override
    public void onInitializeClient() {
        init();
    }
	
}
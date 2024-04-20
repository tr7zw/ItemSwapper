package dev.tr7zw.itemswapper.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public abstract class ItemSwapperSharedServer {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedServer INSTANCE;
    private final ServerItemHandler itemHandler = new ServerItemHandler();

    public void onLoad() {
        INSTANCE = this;
        LOGGER.info("Loading ItemSwapper server support.");
        ServerPlayConnectionEvents.INIT.register((phase, init) -> {
            NetworkUtil.registerServerCustomPacket(SwapItemPayload.class, SwapItemPayload.ID,
                    b -> new SwapItemPayload(b), (p, b) -> p.write(b),
                    (payload, player) -> getItemHandler().swapItem(player, payload));
            NetworkUtil.registerServerCustomPacket(RefillItemPayload.class, RefillItemPayload.ID,
                    b -> new RefillItemPayload(b), (p, b) -> p.write(b),
                    (payload, player) -> getItemHandler().refillSlot(player, payload));
        });
    }

    public ServerItemHandler getItemHandler() {
        return itemHandler;
    }

}

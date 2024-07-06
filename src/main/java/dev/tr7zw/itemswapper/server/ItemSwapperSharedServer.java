package dev.tr7zw.itemswapper.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import dev.tr7zw.itemswapper.util.ServerNetworkUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public abstract class ItemSwapperSharedServer {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedServer INSTANCE;
    private final ServerItemHandler itemHandler = new ServerItemHandler();

    public void onLoad() {
        INSTANCE = this;
        LOGGER.info("Loading ItemSwapper server support.");
        ServerPlayConnectionEvents.INIT.register((phase, init) -> {
            ServerNetworkUtil.registerClientCustomPacket(ShulkerSupportPayload.class, ShulkerSupportPayload.ID,
                    b -> new ShulkerSupportPayload(b), (p, b) -> p.write(b));
            ServerNetworkUtil.registerClientCustomPacket(RefillSupportPayload.class, RefillSupportPayload.ID,
                    b -> new RefillSupportPayload(b), (p, b) -> p.write(b));
            ServerNetworkUtil.registerClientCustomPacket(DisableModPayload.class, DisableModPayload.ID,
                    b -> new DisableModPayload(b), (p, b) -> p.write(b));
            ServerNetworkUtil.registerServerCustomPacket(SwapItemPayload.class, SwapItemPayload.ID,
                    b -> new SwapItemPayload(b), (p, b) -> p.write(b),
                    (payload, player) -> getItemHandler().swapItem(player, payload));
            ServerNetworkUtil.registerServerCustomPacket(RefillItemPayload.class, RefillItemPayload.ID,
                    b -> new RefillItemPayload(b), (p, b) -> p.write(b),
                    (payload, player) -> getItemHandler().refillSlot(player, payload));
        });
    }

    public ServerItemHandler getItemHandler() {
        return itemHandler;
    }

}

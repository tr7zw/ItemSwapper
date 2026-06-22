package dev.tr7zw.itemswapper.server;

import dev.tr7zw.itemswapper.packets.clientbound.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.itemswapper.server.manger.*;
import dev.tr7zw.itemswapper.server.provider.*;
import dev.tr7zw.transition.loader.networking.*;
import lombok.*;
import org.apache.logging.log4j.*;

public abstract class ItemSwapperSharedServer {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedServer INSTANCE;
    private final ServerProviderManager providerManager = new ServerProviderManager();
    @Getter
    private final ServerPlayerManager playerManager = new ServerPlayerManager();
    private final ServerItemHandler itemHandler = new ServerItemHandler(providerManager, playerManager);

    public void onLoad() {
        INSTANCE = this;
        LOGGER.info("Loading ItemSwapper server support.");
        providerManager.registerProvider(new ShulkerContainerProvider());
        ServerNetworkUtil.registerPackets(handler -> {
            // Client packets
            handler.registerClientCustomPacket(ShulkerSupportPayload.INSTANCE);
            handler.registerClientCustomPacket(RefillSupportPayload.INSTANCE);
            handler.registerClientCustomPacket(DisableModPayload.INSTANCE);
            handler.registerClientCustomPacket(ItemAvailability.INSTANCE);
            // Server packets
            handler.registerServerCustomPacket(SwapItemPayload.INSTANCE,
                    (payload, player) -> getItemHandler().swapItem(player, payload));
            handler.registerServerCustomPacket(RefillItemPayload.INSTANCE,
                    (payload, player) -> getItemHandler().refillSlot(player, payload));
            handler.registerServerCustomPacket(RequestAvailability.INSTANCE,
                    (payload, player) -> getItemHandler().processAvailability(player, payload));
            handler.registerServerCustomPacket(EmptySlotPayload.INSTANCE, (payload, player) -> getItemHandler()
                    .storeAwayItem(player, payload.slot(), payload.itemListing().asItemSet()));
            handler.registerServerCustomPacket(SwitchToItemPayload.INSTANCE,
                    (payload, player) -> getItemHandler().switchToItem(player, payload));
            handler.registerServerCustomPacket(RequestAnyItemPayload.INSTANCE,
                    (payload, player) -> getItemHandler().switchToAnyItem(player, payload));
        });
    }

    public ServerItemHandler getItemHandler() {
        return itemHandler;
    }

}

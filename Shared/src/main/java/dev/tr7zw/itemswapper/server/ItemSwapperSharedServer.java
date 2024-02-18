package dev.tr7zw.itemswapper.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ItemSwapperSharedServer {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedServer INSTANCE;
    private final ServerItemHandler itemHandler = new ServerItemHandler();

    public void onLoad() {
        INSTANCE = this;
        LOGGER.info("Loading ItemSwapper server support.");

    }

    public ServerItemHandler getItemHandler() {
        return itemHandler;
    }

}

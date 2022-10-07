package dev.tr7zw.itemswapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.DedicatedServerModInitializer;

public class ItemSwapperServerMod implements DedicatedServerModInitializer{

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    
    @Override
    public void onInitializeServer() {
        LOGGER.info("Loading ItemSwapper server support.");
    }

}

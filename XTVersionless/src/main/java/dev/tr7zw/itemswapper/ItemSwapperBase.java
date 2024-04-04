package dev.tr7zw.itemswapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.itemswapper.config.CacheManager;
import dev.tr7zw.itemswapper.config.ConfigManager;

public class ItemSwapperBase {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static final String MODID = "itemswapper";
    protected final ConfigManager configManager = ConfigManager.getInstance();
    protected final CacheManager cacheManager = CacheManager.getInstance();

    public ItemSwapperBase() {
        super();
    }

}
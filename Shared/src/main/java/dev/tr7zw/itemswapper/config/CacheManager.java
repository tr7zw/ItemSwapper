package dev.tr7zw.itemswapper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class CacheManager {
    private CacheServerAddresses cache;
    private static final CacheManager INSTANCE = new CacheManager();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File cacheFile = new File("config", "itemswapper-server-cache.json");

    private CacheManager() {
        if (cacheFile.exists()) {
            try {
                cache = gson.fromJson(Files.readString(cacheFile.toPath()), CacheServerAddresses.class);
            } catch (JsonSyntaxException | IOException exception) {
                ItemSwapperSharedMod.LOGGER.warn("Error while loading config: " + exception.getMessage());
                ItemSwapperSharedMod.LOGGER.warn("A new configuration will be created!");
            }
        }

        if (cache == null) {
            reset();
        }
    }

    public CacheServerAddresses getCache() {
        return cache;
    }

    public static CacheManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        cache = new CacheServerAddresses();
        writeConfig();
    }

    public void writeConfig() {
        if (cacheFile.exists()) {
            boolean isDeleted = cacheFile.delete();
            ItemSwapperSharedMod.LOGGER.debug("Config could be deleted before writing to it: " + isDeleted);
        }

        try {
            Files.writeString(cacheFile.toPath(), gson.toJson(cache));
        } catch (IOException ioException) {
            ItemSwapperSharedMod.LOGGER.warn(ioException.getMessage());
        }
    }
}

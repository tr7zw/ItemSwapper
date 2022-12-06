package dev.tr7zw.itemswapper.config;

import com.google.gson.JsonSyntaxException;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ConfigManager {

    private Config config;
    private static final ConfigManager INSTANCE = new ConfigManager();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile = new File("config", "itemswapper.json");

    private ConfigManager() {
        if (configFile.exists()) {
            try {
                config = gson.fromJson(Files.readString(configFile.toPath()), Config.class);
            } catch (JsonSyntaxException | IOException exception) {
                ItemSwapperSharedMod.LOGGER.warn("Error while loading config: " + exception.getMessage());
                ItemSwapperSharedMod.LOGGER.warn("A new configuration will be created!");
            }
        }

        if (config == null) {
            reset();
        } else {
            if (ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
    }

    public Config getConfig() {
        return config;
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        config = new Config();
        writeConfig();
    }

    public void writeConfig() {
        if (configFile.exists()) {
            boolean isDeleted = configFile.delete();
            ItemSwapperSharedMod.LOGGER.debug("Config could be deleted before writing to it: " + isDeleted);
        }

        try {
            Files.writeString(configFile.toPath(), gson.toJson(config));
        } catch (IOException ioException) {
            ItemSwapperSharedMod.LOGGER.warn(ioException.getMessage());
        }
    }

}

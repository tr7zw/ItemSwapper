package dev.tr7zw.itemswapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ConfigManager {

    private static ConfigManager instance = new ConfigManager();
    
    public static ConfigManager getInstance() {
        return instance;
    }
    
    private Config config;
    private final File settingsFile = new File("config", "itemswapper.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    private ConfigManager() {
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if(ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
    }
    
    public Config getConfig() {
        return config;
    }
    
    public void reset() {
        config = new Config();
        writeConfig();
    }
    
    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
}

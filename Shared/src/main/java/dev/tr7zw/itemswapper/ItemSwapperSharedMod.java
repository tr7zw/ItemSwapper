package dev.tr7zw.itemswapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;

public abstract class ItemSwapperSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedMod instance;
    private Minecraft minecraft = Minecraft.getInstance();
    private boolean enableShulkers = false;

    private ItemGroupManager itemGroupManager = new ItemGroupManager();
    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "ItemSwapper");
    protected boolean pressed = false;
    public Config config;
    private final File settingsFile = new File("config", "itemswapper.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        instance = this;
        LOGGER.info("Loading ItemSwapper!");
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
        initModloader();
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
    
    public void clientTick() {
        Overlay overlay = Minecraft.getInstance().getOverlay();
        if (keybind.isDown()) {
            if (!pressed && overlay == null) {
                Item itemInHand = minecraft.player.getMainHandItem().getItem();
                Item[] entries = itemGroupManager.getList(itemInHand);
                if (entries != null) {
                    Minecraft.getInstance().setOverlay(new ItemListOverlay(entries));
                    pressed = true;
                } else {
                    entries = itemGroupManager.getSelection(itemInHand);
                    if (entries != null) {
                        Minecraft.getInstance().setOverlay(
                                new SwitchItemOverlay(entries, itemGroupManager.getSecondarySelection(itemInHand)));
                        pressed = true;
                    } else {
                        // Fallback for if there is just a second set, no first set
                        entries = itemGroupManager.getSecondarySelection(itemInHand);
                        if (entries != null) {
                            Minecraft.getInstance().setOverlay(new SwitchItemOverlay(entries, null));
                            pressed = true;
                        }
                    }
                }
            } else if (!pressed && overlay instanceof XTOverlay xtOverlay) {
                  xtOverlay.onClose();
                  Minecraft.getInstance().setOverlay(null);
                  pressed = true;
            }
        } else {
            pressed = false;
            if (!config.toggleMode && overlay instanceof XTOverlay xtOverlay) {
                xtOverlay.onClose();
                Minecraft.getInstance().setOverlay(null);
            }
        }
    }
    
    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.itemswapper.title") {

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getOnOffOption("text.itemswapper.toggleMode", () -> config.toggleMode,
                        (b) -> config.toggleMode = b));
              
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                
            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

        return screen;
    }

    public abstract void initModloader();

    public ItemGroupManager getItemGroupManager() {
        return itemGroupManager;
    }

    public void setEnableShulkers(boolean value) {
        this.enableShulkers = value;
    }

    public boolean areShulkersEnabled() {
        return this.enableShulkers;
    }

}

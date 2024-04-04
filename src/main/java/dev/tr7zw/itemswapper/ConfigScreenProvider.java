package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.overlay.EditListScreen;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenProvider {

    private static final ConfigManager configManager = ConfigManager.getInstance();

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.itemswapper.title") {

            private CustomConfigScreen inst = this;

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getOnOffOption("text.itemswapper.showTooltips",
                        () -> configManager.getConfig().showTooltips, b -> configManager.getConfig().showTooltips = b));
                options.add(getOnOffOption("text.itemswapper.toggleMode", () -> configManager.getConfig().toggleMode,
                        b -> configManager.getConfig().toggleMode = b));
                options.add(getOnOffOption("text.itemswapper.showCursor", () -> configManager.getConfig().showCursor,
                        b -> configManager.getConfig().showCursor = b));
                options.add(getOnOffOption("text.itemswapper.editMode", () -> configManager.getConfig().editMode,
                        b -> configManager.getConfig().editMode = b));
                options.add(getOnOffOption("text.itemswapper.creativeCheatMode",
                        () -> configManager.getConfig().creativeCheatMode,
                        b -> configManager.getConfig().creativeCheatMode = b));
                options.add(getOnOffOption("text.itemswapper.ignoreHotbar",
                        () -> configManager.getConfig().ignoreHotbar, b -> configManager.getConfig().ignoreHotbar = b));
                options.add(getOnOffOption("text.itemswapper.unlockListMouse",
                        () -> configManager.getConfig().unlockListMouse,
                        b -> configManager.getConfig().unlockListMouse = b));
                options.add(getOnOffOption("text.itemswapper.disableShulkers",
                        () -> configManager.getConfig().disableShulkers,
                        b -> configManager.getConfig().disableShulkers = b));

                options.add(getDoubleOption("text.itemswapper.controllerSpeed", 1, 16, 0.1f,
                        () -> (double) configManager.getConfig().controllerSpeed,
                        d -> configManager.getConfig().controllerSpeed = d.floatValue()));
                options.add(getDoubleOption("text.itemswapper.mouseSpeed", 0.1f, 3, 0.1f,
                        () -> (double) configManager.getConfig().mouseSpeed,
                        d -> configManager.getConfig().mouseSpeed = d.floatValue()));
                options.add(getOnOffOption("text.itemswapper.fallbackInventory",
                        () -> configManager.getConfig().fallbackInventory,
                        b -> configManager.getConfig().fallbackInventory = b));

                options.add(getOnOffOption("text.itemswapper.disablePickblockOnToolsWeapons",
                        () -> configManager.getConfig().disablePickblockOnToolsWeapons,
                        b -> configManager.getConfig().disablePickblockOnToolsWeapons = b));

                if (ViveCraftSupport.getInstance().isAvailable()) {
                    options.add(getOnOffOption("text.itemswapper.vivecraftCompat",
                            () -> configManager.getConfig().vivecraftCompat,
                            b -> configManager.getConfig().vivecraftCompat = b));
                }

                options.add(getOnOffOption("text.itemswapper.allowWalkingWithUI",
                        () -> configManager.getConfig().allowWalkingWithUI,
                        b -> configManager.getConfig().allowWalkingWithUI = b));
                options.add(getOnOffOption("text.itemswapper.startOnItem", () -> configManager.getConfig().startOnItem,
                        b -> configManager.getConfig().startOnItem = b));
                options.add(getOnOffOption("text.itemswapper.alwaysInventory",
                        () -> configManager.getConfig().alwaysInventory,
                        b -> configManager.getConfig().alwaysInventory = b));
                options.add(getOnOffOption("text.itemswapper.showHotbar", () -> configManager.getConfig().showHotbar,
                        b -> configManager.getConfig().showHotbar = b));
                options.add(getOnOffOption("text.itemswapper.autoPalette",
                        () -> configManager.getConfig().experimentalAutoPalette,
                        b -> configManager.getConfig().experimentalAutoPalette = b));

                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                this.addRenderableWidget(
                        Button.builder(ComponentProvider.translatable("text.itemswapper.whitelist"), new OnPress() {
                            @Override
                            public void onPress(Button button) {
                                Minecraft.getInstance()
                                        .setScreen(new EditListScreen(inst, Minecraft.getInstance().options, true));
                            }
                        }).pos(this.width / 2 - 210, this.height - 27).size(50, 20).build());
                this.addRenderableWidget(
                        Button.builder(ComponentProvider.translatable("text.itemswapper.blacklist"), new OnPress() {
                            @Override
                            public void onPress(Button button) {
                                Minecraft.getInstance()
                                        .setScreen(new EditListScreen(inst, Minecraft.getInstance().options, false));
                            }
                        }).pos(this.width / 2 - 160, this.height - 27).size(50, 20).build());
            }

            @Override
            public void save() {
                configManager.writeConfig();
            }

            @Override
            public void reset() {
                configManager.reset();
            }

        };
    }

}

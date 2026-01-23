package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.CacheManager;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.config.PickBlockMode;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WLabel;
import dev.tr7zw.trender.gui.widget.WListPanel;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.WWidget;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.trender.gui.widget.icon.ItemIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.Items;

public class ConfigScreenProvider {

    private static final ConfigManager configManager = ConfigManager.getInstance();

    public static Screen createConfigScreen(Screen parent) {
        return new ConfigScreen(parent).createScreen();
    }

    private static class ConfigScreen extends AbstractConfigScreen {

        public ConfigScreen(Screen previous) {
            super(ComponentProvider.translatable("text.itemswapper.title"), previous);
            WGridPanel root = new WGridPanel(8);
            root.setInsets(Insets.ROOT_PANEL);
            setRootPanel(root);
            WTabPanel wTabPanel = new WTabPanel();

            // options page
            List<OptionInstance> options = new ArrayList<>();
            options.add(getSplitLine("text.itemswapper.category.visuals"));
            options.add(getOnOffOption("text.itemswapper.showCursor", () -> configManager.getConfig().showCursor,
                    b -> configManager.getConfig().showCursor = b));
            options.add(getOnOffOption("text.itemswapper.showTooltips", () -> configManager.getConfig().showTooltips,
                    b -> configManager.getConfig().showTooltips = b));

            options.add(getSplitLine("text.itemswapper.category.controls"));
            options.add(getOnOffOption("text.itemswapper.toggleMode", () -> configManager.getConfig().toggleMode,
                    b -> configManager.getConfig().toggleMode = b));
            options.add(
                    getOnOffOption("text.itemswapper.unlockListMouse", () -> configManager.getConfig().unlockListMouse,
                            b -> configManager.getConfig().unlockListMouse = b));
            options.add(getDoubleOption("text.itemswapper.controllerSpeed", 1, 16, 0.1f,
                    () -> configManager.getConfig().controllerSpeed,
                    d -> configManager.getConfig().controllerSpeed = (float) d));
            options.add(getDoubleOption("text.itemswapper.mouseSpeed", 0.1f, 3, 0.1f,
                    () -> configManager.getConfig().mouseSpeed, d -> configManager.getConfig().mouseSpeed = (float) d));

            options.add(getSplitLine("text.itemswapper.category.behavior"));
            options.add(getOnOffOption("text.itemswapper.creativeCheatMode",
                    () -> configManager.getConfig().creativeCheatMode,
                    b -> configManager.getConfig().creativeCheatMode = b));
            options.add(getOnOffOption("text.itemswapper.ignoreHotbar", () -> configManager.getConfig().ignoreHotbar,
                    b -> configManager.getConfig().ignoreHotbar = b));
            options.add(
                    getOnOffOption("text.itemswapper.disableShulkers", () -> configManager.getConfig().disableShulkers,
                            b -> configManager.getConfig().disableShulkers = b));
            options.add(getOnOffOption("text.itemswapper.showOpenInventoryButton",
                    () -> configManager.getConfig().showOpenInventoryButton,
                    b -> configManager.getConfig().showOpenInventoryButton = b));
            options.add(getOnOffOption("text.itemswapper.fallbackInventory",
                    () -> configManager.getConfig().fallbackInventory,
                    b -> configManager.getConfig().fallbackInventory = b));
            options.add(getEnumOption("text.itemswapper.disablePickblockOnToolsWeapons", PickBlockMode.class,
                    () -> configManager.getConfig().pickblockOnToolsWeapons,
                    b -> configManager.getConfig().pickblockOnToolsWeapons = b));
            options.add(getOnOffOption("text.itemswapper.allowWalkingWithUI",
                    () -> configManager.getConfig().allowWalkingWithUI,
                    b -> configManager.getConfig().allowWalkingWithUI = b));
            options.add(getOnOffOption("text.itemswapper.startOnItem", () -> configManager.getConfig().startOnItem,
                    b -> configManager.getConfig().startOnItem = b));
            options.add(
                    getOnOffOption("text.itemswapper.alwaysInventory", () -> configManager.getConfig().alwaysInventory,
                            b -> configManager.getConfig().alwaysInventory = b));
            options.add(getOnOffOption("text.itemswapper.showHotbar", () -> configManager.getConfig().showHotbar,
                    b -> configManager.getConfig().showHotbar = b));
            options.add(
                    getOnOffOption("text.itemswapper.rememberPalette", () -> configManager.getConfig().rememberPalette,
                            b -> configManager.getConfig().rememberPalette = b));
            options.add(getOnOffOption("text.itemswapper.autoPalette",
                    () -> configManager.getConfig().experimentalAutoPalette,
                    b -> configManager.getConfig().experimentalAutoPalette = b));
            options.add(getOnOffOption("text.itemswapper.listsAsPalette",
                    () -> configManager.getConfig().listsAsPalette,
                    b -> configManager.getConfig().listsAsPalette = b));

            options.add(getSplitLine("text.itemswapper.category.misc"));
            options.add(getOnOffOption("text.itemswapper.editMode", () -> configManager.getConfig().editMode,
                    b -> configManager.getConfig().editMode = b));
            if (ViveCraftSupport.getInstance().isAvailable()) {
                options.add(getOnOffOption("text.itemswapper.vivecraftCompat",
                        () -> configManager.getConfig().vivecraftCompat,
                        b -> configManager.getConfig().vivecraftCompat = b));
            }

            var optionList = createOptionList(options);
            optionList.setGap(-1);
            optionList.setSize(14 * 20, 9 * 20);

            wTabPanel.add(optionList, b -> b.title(ComponentProvider.translatable("key.itemswapper.tab.options")));

            wTabPanel.add(getList(CacheManager.getInstance().getCache().disableOnIp),
                    b -> b.title(ComponentProvider.translatable("text.itemswapper.blacklist")));
            wTabPanel.add(getList(CacheManager.getInstance().getCache().enableOnIp),
                    b -> b.title(ComponentProvider.translatable("text.itemswapper.whitelist")));

            root.add(wTabPanel, 0, 1);

            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                save();
                Minecraft.getInstance().setScreen(previous);
            });
            root.add(doneButton, 0, 26, 6, 2);

            WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
            resetButton.setOnClick(() -> {
                reset();
                root.layout();
            });
            root.add(resetButton, 29, 26, 6, 2);

            root.setBackgroundPainter(BackgroundPainter.VANILLA);

            root.validate(this);
            root.setHost(this);
        }

        public WWidget getList(List<String> list) {
            WListPanel<String, WGridPanel> listPanel = new WListPanel<String, WGridPanel>(list, () -> new WGridPanel(),
                    (s, panel) -> {
                        panel.setGaps(-17, -17);
                        WButton button = new WButton(new ItemIcon(Items.BARRIER));
                        button.setToolip(ComponentProvider.translatable("text.itemswapper.remove"));
                        button.setOnClick(() -> {
                            list.remove(s);
                            panel.getParent().layout();
                            CacheManager.getInstance().writeConfig();
                        });
                        panel.add(button, 0, 0, 2, 2);
                        panel.add(new WLabel(ComponentProvider.literal(s)), 23, 5);
                    });
            listPanel.setGap(0);
            return listPanel;
        }

        @Override
        public void save() {
            configManager.writeConfig();
        }

        @Override
        public void reset() {
            configManager.reset();
        }
    }

}

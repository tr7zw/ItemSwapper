package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.transition.config.*;
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

    private static final ConfigHolder configHolder = ConfigHolder.getInstance();

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
            options.add(getOnOffOption("text.itemswapper.showCursor",
                    () -> configHolder.getGeneral().getConfig().showCursor,
                    b -> configHolder.getGeneral().getConfig().showCursor = b));
            options.add(getOnOffOption("text.itemswapper.showTooltips",
                    () -> configHolder.getGeneral().getConfig().showTooltips,
                    b -> configHolder.getGeneral().getConfig().showTooltips = b));

            options.add(getSplitLine("text.itemswapper.category.controls"));
            options.add(getOnOffOption("text.itemswapper.toggleMode",
                    () -> configHolder.getGeneral().getConfig().toggleMode,
                    b -> configHolder.getGeneral().getConfig().toggleMode = b));
            options.add(getOnOffOption("text.itemswapper.unlockListMouse",
                    () -> configHolder.getGeneral().getConfig().unlockListMouse,
                    b -> configHolder.getGeneral().getConfig().unlockListMouse = b));
            options.add(getDoubleOption("text.itemswapper.controllerSpeed", 1, 16, 0.1f,
                    () -> configHolder.getGeneral().getConfig().controllerSpeed,
                    d -> configHolder.getGeneral().getConfig().controllerSpeed = (float) d));
            options.add(getDoubleOption("text.itemswapper.mouseSpeed", 0.1f, 3, 0.1f,
                    () -> configHolder.getGeneral().getConfig().mouseSpeed,
                    d -> configHolder.getGeneral().getConfig().mouseSpeed = (float) d));

            options.add(getSplitLine("text.itemswapper.category.behavior"));
            options.add(getOnOffOption("text.itemswapper.creativeCheatMode",
                    () -> configHolder.getGeneral().getConfig().creativeCheatMode,
                    b -> configHolder.getGeneral().getConfig().creativeCheatMode = b));
            options.add(getOnOffOption("text.itemswapper.ignoreHotbar",
                    () -> configHolder.getGeneral().getConfig().ignoreHotbar,
                    b -> configHolder.getGeneral().getConfig().ignoreHotbar = b));
            options.add(getOnOffOption("text.itemswapper.disableShulkers",
                    () -> configHolder.getGeneral().getConfig().disableShulkers,
                    b -> configHolder.getGeneral().getConfig().disableShulkers = b));
            options.add(getOnOffOption("text.itemswapper.showOpenInventoryButton",
                    () -> configHolder.getGeneral().getConfig().showOpenInventoryButton,
                    b -> configHolder.getGeneral().getConfig().showOpenInventoryButton = b));
            options.add(getOnOffOption("text.itemswapper.fallbackInventory",
                    () -> configHolder.getGeneral().getConfig().fallbackInventory,
                    b -> configHolder.getGeneral().getConfig().fallbackInventory = b));
            options.add(getEnumOption("text.itemswapper.disablePickblockOnToolsWeapons", PickBlockMode.class,
                    () -> configHolder.getGeneral().getConfig().pickblockOnToolsWeapons,
                    b -> configHolder.getGeneral().getConfig().pickblockOnToolsWeapons = b));
            options.add(getOnOffOption("text.itemswapper.allowWalkingWithUI",
                    () -> configHolder.getGeneral().getConfig().allowWalkingWithUI,
                    b -> configHolder.getGeneral().getConfig().allowWalkingWithUI = b));
            options.add(getOnOffOption("text.itemswapper.startOnItem",
                    () -> configHolder.getGeneral().getConfig().startOnItem,
                    b -> configHolder.getGeneral().getConfig().startOnItem = b));
            options.add(getOnOffOption("text.itemswapper.alwaysInventory",
                    () -> configHolder.getGeneral().getConfig().alwaysInventory,
                    b -> configHolder.getGeneral().getConfig().alwaysInventory = b));
            options.add(getOnOffOption("text.itemswapper.showHotbar",
                    () -> configHolder.getGeneral().getConfig().showHotbar,
                    b -> configHolder.getGeneral().getConfig().showHotbar = b));
            options.add(getOnOffOption("text.itemswapper.rememberPalette",
                    () -> configHolder.getGeneral().getConfig().rememberPalette,
                    b -> configHolder.getGeneral().getConfig().rememberPalette = b));
            options.add(getOnOffOption("text.itemswapper.autoPalette",
                    () -> configHolder.getGeneral().getConfig().experimentalAutoPalette,
                    b -> configHolder.getGeneral().getConfig().experimentalAutoPalette = b));
            options.add(getOnOffOption("text.itemswapper.listsAsPalette",
                    () -> configHolder.getGeneral().getConfig().listsAsPalette,
                    b -> configHolder.getGeneral().getConfig().listsAsPalette = b));

            options.add(getSplitLine("text.itemswapper.category.misc"));
            options.add(
                    getOnOffOption("text.itemswapper.editMode", () -> configHolder.getGeneral().getConfig().editMode,
                            b -> configHolder.getGeneral().getConfig().editMode = b));
            if (ViveCraftSupport.getInstance().isAvailable()) {
                options.add(getOnOffOption("text.itemswapper.vivecraftCompat",
                        () -> configHolder.getGeneral().getConfig().vivecraftCompat,
                        b -> configHolder.getGeneral().getConfig().vivecraftCompat = b));
            }

            var optionList = createOptionList(options);
            optionList.setGap(-1);
            optionList.setSize(14 * 20, 9 * 20);

            wTabPanel.add(optionList, b -> b.title(ComponentProvider.translatable("key.itemswapper.tab.options")));

            wTabPanel.add(getList(configHolder.getServerCache().getConfig().disableOnIp),
                    b -> b.title(ComponentProvider.translatable("text.itemswapper.blacklist")));
            wTabPanel.add(getList(configHolder.getServerCache().getConfig().enableOnIp),
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
                            configHolder.save();
                        });
                        panel.add(button, 0, 0, 2, 2);
                        panel.add(new WLabel(ComponentProvider.literal(s)), 23, 5);
                    });
            listPanel.setGap(0);
            return listPanel;
        }

        @Override
        public void save() {
            configHolder.save();
        }

        @Override
        public void reset() {
            configHolder.reset();
        }
    }

}

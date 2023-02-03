package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import dev.tr7zw.itemswapper.provider.InstrumentItemNameProvider;
import dev.tr7zw.itemswapper.provider.PotionNameProvider;
import dev.tr7zw.itemswapper.provider.RecordNameProvider;
import dev.tr7zw.itemswapper.provider.ShulkerContainerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public abstract class ItemSwapperSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static final String MODID = "itemswapper";

    public static ItemSwapperSharedMod instance;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final ItemGroupManager itemGroupManager = new ItemGroupManager();
    private final ClientProviderManager clientProviderManager = new ClientProviderManager();
    private boolean enableShulkers = false;
    private boolean modDisabled = false;
    private boolean bypassExcepted = false;
    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "itemswapper.controlls");
    private boolean pressed = false;
    private boolean lateInitCompleted = false;

    public void init() {
        instance = this;
        LOGGER.info("Loading ItemSwapper!");

        initModloader();
    }
    
    private void lateInit() {
        clientProviderManager.registerContainerProvider(new ShulkerContainerProvider());
        clientProviderManager.registerNameProvider(new PotionNameProvider());
        clientProviderManager.registerNameProvider(new RecordNameProvider());
        clientProviderManager.registerNameProvider(new InstrumentItemNameProvider());
    }

    public void clientTick() {
        // run this code later, so all other mods are done loading
        if(!lateInitCompleted) {
            lateInitCompleted = true;
            lateInit();
        }
        Overlay overlay = Minecraft.getInstance().getOverlay();
        Screen screen = Minecraft.getInstance().screen;

        if (keybind.isDown()) {
            if(overlay instanceof ItemSwapperUI ui) {
                onPress(ui);
            } else if(screen instanceof ItemSwapperUI ui) {
                onPress(ui);
            } else if(screen != null) {
                // not our screen, don't do anything
            } else {
                onPress(null);
            }
        } else {
            pressed = false;

            if (!configManager.getConfig().toggleMode && overlay instanceof ItemSwapperUI ui) {
                closeScreen(ui);
            }
            if (!configManager.getConfig().toggleMode && screen instanceof ItemSwapperUI ui) {
                closeScreen(ui);
            }
        }
    }

    private void onPress(ItemSwapperUI overlay) {
        if (!itemGroupManager.isResourcepackSelected()) {
            this.minecraft.player.displayClientMessage(
                    Component.translatable("text.itemswapper.resourcepack.notSelected").withStyle(ChatFormatting.RED),
                    true);
        }

        if (!pressed && isModDisabled()) {
            pressed = true;
            this.minecraft.gui.setOverlayMessage(
                    Component.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED), false);
            return;
        }

        if (!pressed && !enableShulkers && !bypassExcepted) {
            this.minecraft.setScreen(
                    new ConfirmScreen(this::acceptBypassCallback,
                            Component.translatable("text.itemswapper.confirm.title"),
                            Component.translatable("text.itemswapper.confirm.description")));
            pressed = true;
            return;
        }

        if (!pressed && overlay == null) {
            if (couldOpenScreen()) {
                return;
            }
        } else if (!pressed) {
            closeScreen(overlay);
        }

        pressed = true;
    }

    private boolean couldOpenScreen() {
        if (minecraft.player.getMainHandItem().isEmpty()) {
            openInventoryScreen();
            pressed = true;
            return true;
        }

        Item itemInHand = minecraft.player.getMainHandItem().getItem();
        Item[] entries = itemGroupManager.getList(itemInHand);

        if (entries != null) {
            openListSwitchScreen(new ItemListOverlay(entries));
        } else {
            ItemGroup group = itemGroupManager.getItemPage(itemInHand);
            if (group != null) {
                openSquareSwitchScreen(group);
            }
        }
        return false;
    }

    public static void openInventoryScreen() {
        if(Minecraft.getInstance().screen instanceof SwitchItemOverlay overlay) {
            overlay.openInventory();
            Minecraft.getInstance().getSoundManager().resume();
            Minecraft.getInstance().mouseHandler.grabMouse();
            return;
        }
        Minecraft.getInstance().setScreen(SwitchItemOverlay.createInventoryOverlay());
        Minecraft.getInstance().getSoundManager().resume();
        Minecraft.getInstance().mouseHandler.grabMouse();
    }

    public static void openListSwitchScreen(ItemListOverlay entries) {
        Minecraft.getInstance().setScreen(entries);
        Minecraft.getInstance().getSoundManager().resume();
        Minecraft.getInstance().mouseHandler.grabMouse();
    }

    public void openSquareSwitchScreen(ItemGroup group) {
        if(Minecraft.getInstance().screen instanceof SwitchItemOverlay overlay) {
            overlay.openItemGroup(group);
            return;
        }
        Minecraft.getInstance().setScreen(SwitchItemOverlay.createPaletteOverlay(group));
        Minecraft.getInstance().getSoundManager().resume();
        Minecraft.getInstance().mouseHandler.grabMouse();
    }

    public static void closeScreen(@NotNull ItemSwapperUI xtOverlay) {
        xtOverlay.onOverlayClose();
        if(xtOverlay instanceof Overlay) {
            Minecraft.getInstance().setOverlay(null);
        } else if (xtOverlay instanceof Screen) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    public Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.itemswapper.title") {

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getOnOffOption("text.itemswapper.toggleMode", () -> configManager.getConfig().toggleMode,
                        b -> configManager.getConfig().toggleMode = b));
                options.add(getOnOffOption("text.itemswapper.showCursor", () -> configManager.getConfig().showCursor,
                        b -> configManager.getConfig().showCursor = b));
                options.add(getOnOffOption("text.itemswapper.editMode", () -> configManager.getConfig().editMode,
                        b -> configManager.getConfig().editMode = b));
                options.add(getOnOffOption("text.itemswapper.creativeCheatMode",
                        () -> configManager.getConfig().creativeCheatMode,
                        b -> configManager.getConfig().creativeCheatMode = b));
                options.add(
                        getOnOffOption("text.itemswapper.ignoreHotbar", () -> configManager.getConfig().ignoreHotbar,
                                b -> configManager.getConfig().ignoreHotbar = b));
                options.add(
                        getOnOffOption("text.itemswapper.unlockListMouse",
                                () -> configManager.getConfig().unlockListMouse,
                                b -> configManager.getConfig().unlockListMouse = b));
                options.add(
                        getOnOffOption("text.itemswapper.disableShulkers",
                                () -> configManager.getConfig().disableShulkers,
                                b -> configManager.getConfig().disableShulkers = b));
                
                options.add(getDoubleOption("text.itemswapper.controllerSpeed", 1, 16, 0.1f, () -> (double)configManager.getConfig().controllerSpeed, d -> configManager.getConfig().controllerSpeed = d.floatValue()));
                options.add(getDoubleOption("text.itemswapper.mouseSpeed", 0.1f, 3, 0.1f, () -> (double)configManager.getConfig().mouseSpeed, d -> configManager.getConfig().mouseSpeed = d.floatValue()));
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
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

    public void setBypassExcepted(boolean bypassExcepted) {
        this.bypassExcepted = bypassExcepted;
    }

    public void setModDisabled(boolean value) {
        this.modDisabled = value;
    }

    public boolean isModDisabled() {
        return this.modDisabled;
    }

    private void acceptBypassCallback(boolean accepted) {
        if (accepted) {
            bypassExcepted = true;
        }

        this.minecraft.setScreen(null);
    }

    public ClientProviderManager getClientProviderManager() {
        return clientProviderManager;
    }
    
    public KeyMapping getKeybind() {
        return keybind;
    }
}

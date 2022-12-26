package dev.tr7zw.itemswapper;

import dev.tr7zw.itemswapper.config.ConfigManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.InventorySwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.SquareSwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
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
import org.jetbrains.annotations.NotNull;

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
    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "ItemSwapper");
    private boolean pressed = false;

    public void init() {
        instance = this;
        LOGGER.info("Loading ItemSwapper!");

        initModloader();
        clientProviderManager.registerContainerProvider(new ShulkerContainerProvider());
        clientProviderManager.registerNameProvider(new PotionNameProvider());
        clientProviderManager.registerNameProvider(new RecordNameProvider());
    }

    public void clientTick() {
        Overlay overlay = Minecraft.getInstance().getOverlay();

        if (keybind.isDown()) {
            onPress(overlay);
        } else {
            pressed = false;

            if (!configManager.getConfig().toggleMode && overlay instanceof XTOverlay xtOverlay) {
                closeScreen(xtOverlay);
            }
        }
    }

    private void onPress(Overlay overlay) {
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
        } else if (!pressed && overlay instanceof XTOverlay xtOverlay) {
            closeScreen(xtOverlay);
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

    private static void openInventoryScreen() {
        Minecraft.getInstance().setOverlay(new InventorySwitchItemOverlay());
    }

    private static void openListSwitchScreen(ItemListOverlay entries) {
        Minecraft.getInstance().setOverlay(entries);
    }

    public void openSquareSwitchScreen(ItemGroup group) {
        Minecraft.getInstance().setOverlay(new SquareSwitchItemOverlay(group));
    }

    private static void closeScreen(@NotNull XTOverlay xtOverlay) {
        xtOverlay.onClose();
        openListSwitchScreen(null);
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
}

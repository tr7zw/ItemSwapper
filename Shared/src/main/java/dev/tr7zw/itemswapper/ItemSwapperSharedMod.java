package dev.tr7zw.itemswapper;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.config.CacheManager;
import dev.tr7zw.itemswapper.config.ConfigManager;
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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class ItemSwapperSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static final String MODID = "itemswapper";

    public static ItemSwapperSharedMod instance;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ConfigManager configManager = ConfigManager.getInstance();
    private final CacheManager cacheManager = CacheManager.getInstance();
    private final ItemGroupManager itemGroupManager = new ItemGroupManager();
    private final ClientProviderManager clientProviderManager = new ClientProviderManager();
    private final List<String> enableOnIp = cacheManager.getCache().enableOnIp;
    private final List<String> disableOnIp = cacheManager.getCache().disableOnIp;

    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "ItemSwapper");

    private boolean enableShulkers = false;
    private boolean modDisabled = false;
    private boolean disabledByPlayer = false;
    private boolean bypassExcepted = false;
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
        ServerData server = Minecraft.getInstance().getCurrentServer();

        if (server != null && disableOnIp.contains(server.ip) && !disabledByPlayer) {
            setDisabledByPlayer(true);
            LOGGER.info("Itemswapper is deactivated for the server {}, because the player did not accept the warning!", server.ip);
        } else if (isModDisabled()) {
            this.minecraft.gui.setOverlayMessage(
                    Component.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED), false);
        }  else if (keybind.isDown()) {
            onPress(overlay);
        } else {
            pressed = false;

            if (!configManager.getConfig().toggleMode && overlay instanceof XTOverlay xtOverlay) {
                closeOverlay(xtOverlay);
            }
        }
    }

    private void onPress(Overlay overlay) {
        if (this.minecraft.player != null && !itemGroupManager.isResourcepackSelected()) {
            this.minecraft.player.displayClientMessage(
                    Component.translatable("text.itemswapper.resourcepack.notSelected").withStyle(ChatFormatting.RED),
                    true);
        }

        if (!pressed) {
            ServerData server = Minecraft.getInstance().getCurrentServer();

            if (isDisabledByPlayer()) {
                this.minecraft.gui.setOverlayMessage(
                        Component.translatable("text.itemswapper.disabledByPlayer").withStyle(ChatFormatting.RED), false);
            } else if (server != null && !enableOnIp.contains(server.ip) && !enableShulkers && !bypassExcepted) {
                openConfirmationScreen();
            } else if (overlay == null) {
                if (couldOpenScreen()) {
                    return;
                }
            } else if (overlay instanceof XTOverlay xtOverlay) {
                closeOverlay(xtOverlay);
            }

            pressed = true;
        }
    }

    private void openConfirmationScreen() {
        this.minecraft.setScreen(
                new ConfirmScreen(this::acceptBypassCallback, Component.translatable("text.itemswapper.confirm.title"),
                        Component.translatable("text.itemswapper.confirm.description")));
    }

    private boolean couldOpenScreen() {
        if (minecraft.player != null) {
            ItemStack mainHandStack = minecraft.player.getMainHandItem();
            if (minecraft.player != null && mainHandStack.isEmpty()) {
                openInventoryOverlay();
                pressed = true;
                return true;
            }
            Item itemInHand = mainHandStack.getItem();
            Item[] entries = itemGroupManager.getList(itemInHand);

            if (entries != null) {
                openListSwitchOverlay(new ItemListOverlay(entries));
            } else {
                ItemGroup group = itemGroupManager.getItemPage(itemInHand);
                if (group != null) {
                    openSquareSwitchOverlay(group);
                }
            }
        }
        return false;
    }

    private static void openInventoryOverlay() {
        Minecraft.getInstance().setOverlay(new InventorySwitchItemOverlay());
    }

    private static void openListSwitchOverlay(ItemListOverlay entries) {
        Minecraft.getInstance().setOverlay(entries);
    }

    public void openSquareSwitchOverlay(ItemGroup group) {
        Minecraft.getInstance().setOverlay(new SquareSwitchItemOverlay(group));
    }

    private static void closeOverlay(@NotNull XTOverlay xtOverlay) {
        xtOverlay.onClose();
        openListSwitchOverlay(null);
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
                options.add(getOnOffOption("text.itemswapper.unlockListMouse",
                        () -> configManager.getConfig().unlockListMouse,
                        b -> configManager.getConfig().unlockListMouse = b));
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
        ServerData server = Minecraft.getInstance().getCurrentServer();

        if (server != null) {
            if (accepted) {
                bypassExcepted = true;
                cacheManager.getCache().enableOnIp.add(server.ip);
            } else {
                cacheManager.getCache().disableOnIp.add(server.ip);
            }
            cacheManager.writeConfig();
            ItemSwapperSharedMod.LOGGER.info("Add {} to cached ip-addresses", server.ip);
        }
        this.minecraft.setScreen(null);
    }

    public ClientProviderManager getClientProviderManager() {
        return clientProviderManager;
    }

    public boolean isDisabledByPlayer() {
        return disabledByPlayer;
    }

    public void setDisabledByPlayer(boolean disabledByPlayer) {
        this.disabledByPlayer = disabledByPlayer;
    }
}

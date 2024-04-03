package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.accessor.ExtendedMouseHandler;
import dev.tr7zw.itemswapper.compat.AmecsAPISupport;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.CacheManager;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.BlockTextureManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.overlay.EditListScreen;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.provider.InstrumentItemNameProvider;
import dev.tr7zw.itemswapper.provider.PotionNameProvider;
import dev.tr7zw.itemswapper.provider.RecordNameProvider;
import dev.tr7zw.itemswapper.provider.ShulkerContainerProvider;
import dev.tr7zw.itemswapper.provider.SmithingTemplateItemNameProvider;
import dev.tr7zw.util.ComponentProvider;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.item.Item;

public abstract class ItemSwapperSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static final String MODID = "itemswapper";
    private static Minecraft minecraft = Minecraft.getInstance();

    public static ItemSwapperSharedMod instance;

    private final ConfigManager configManager = ConfigManager.getInstance();
    private final CacheManager cacheManager = CacheManager.getInstance();
    private final ItemGroupManager itemGroupManager = new ItemGroupManager();
    private final ClientProviderManager clientProviderManager = new ClientProviderManager();
    @Getter
    private final BlockTextureManager blockTextureManager = new BlockTextureManager();
    private final List<String> enableOnIp = cacheManager.getCache().enableOnIp;
    private final List<String> disableOnIp = cacheManager.getCache().disableOnIp;

    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "ItemSwapper");
    protected KeyMapping openInventoryKeybind = new KeyMapping("key.itemswapper.openInventory",
            InputConstants.UNKNOWN.getValue(), "ItemSwapper");

    private boolean enableShulkers = false;
    private boolean enableRefill = false;
    private boolean modDisabled = false;
    private boolean disabledByPlayer = false;
    private boolean bypassAccepted = false;
    private boolean pressed = false;
    private boolean lateInitCompleted = false;
    private Item lastItem;
    private Page lastPage;

    public void init() {
        instance = this;
        minecraft = Minecraft.getInstance();
        LOGGER.info("Loading ItemSwapper!");

        initModloader();
    }

    private void lateInit() {
        clientProviderManager.registerContainerProvider(new ShulkerContainerProvider());
        clientProviderManager.registerNameProvider(new PotionNameProvider());
        clientProviderManager.registerNameProvider(new RecordNameProvider());
        clientProviderManager.registerNameProvider(new InstrumentItemNameProvider());
        clientProviderManager.registerNameProvider(new SmithingTemplateItemNameProvider());
    }

    public void clientTick() {
        // run this code later, so all other mods are done loading
        if (!lateInitCompleted) {
            lateInitCompleted = true;
            lateInit();
        }
        Screen screen = minecraft.screen;

        ServerData server = Minecraft.getInstance().getCurrentServer();

        if (server != null && disableOnIp.contains(server.ip) && !disabledByPlayer) {
            setDisabledByPlayer(true);
            LOGGER.info("Itemswapper is deactivated for the server {}, because the player did not accept the warning!",
                    server.ip);
        } else if (keybind.isDown()) {
            if (isModDisabled()) {
                minecraft.gui.setOverlayMessage(
                        ComponentProvider.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED),
                        false);
            } else if (screen instanceof ItemSwapperUI ui) {
                onPress(ui);
            } else if (screen != null) {
                // not our screen, don't do anything
            } else {
                onPress(null);
            }
        } else if (openInventoryKeybind.isDown()) {
            if (isModDisabled()) {
                minecraft.gui.setOverlayMessage(
                        ComponentProvider.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED),
                        false);
            } else if (screen == null) {
                ItemSwapperSharedMod.openInventoryScreen();
            }
        } else {
            pressed = false;

            if (screen instanceof ItemSwapperUI ui) {
                if (!configManager.getConfig().toggleMode && !ViveCraftSupport.getInstance().isActive()) {
                    onPrimaryClick(ui, true);
                }
            }
        }
    }

    private void onPress(ItemSwapperUI overlay) {
        // skip this check for alwaysInventory mode
        if (minecraft.player != null && !itemGroupManager.isResourcepackSelected() && !configManager.getConfig().alwaysInventory) {
            minecraft.player.displayClientMessage(ComponentProvider
                    .translatable("text.itemswapper.resourcepack.notSelected").withStyle(ChatFormatting.RED), true);
        }

        if (!pressed && isModDisabled()) {
            pressed = true;
            minecraft.gui.setOverlayMessage(
                    ComponentProvider.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED), false);
            return;
        }

        ServerData server = Minecraft.getInstance().getCurrentServer();
        if (!pressed) {
            if (isDisabledByPlayer()) {
                minecraft.gui.setOverlayMessage(ComponentProvider.translatable("text.itemswapper.disabledByPlayer")
                        .withStyle(ChatFormatting.RED), false);
            } else if (server != null && !enableOnIp.contains(server.ip) && !enableShulkers && !bypassAccepted) {
                openConfirmationScreen();
            } else if (overlay == null) {
                if (!bypassAccepted && server != null && enableOnIp.contains(server.ip)) {
                    bypassAccepted = true;
                    minecraft.gui.setOverlayMessage(ComponentProvider.translatable("text.itemswapper.usedwhitelist")
                            .withStyle(ChatFormatting.GOLD), false);
                }
                if (couldOpenScreen()) {
                    pressed = true;
                    return;
                }
            } else {
                onPrimaryClick(overlay, true);
            }
        }
        pressed = true;
    }

    private void openConfirmationScreen() {
        minecraft.setScreen(new ConfirmScreen(this::acceptBypassCallback,
                ComponentProvider.translatable("text.itemswapper.confirm.title"),
                ComponentProvider.translatable("text.itemswapper.confirm.description")));
    }

    private boolean couldOpenScreen() {
        if (minecraft.player.getMainHandItem().isEmpty() || configManager.getConfig().alwaysInventory) {
            openInventoryScreen();
            return true;
        }

        Item itemInHand = minecraft.player.getMainHandItem().getItem();
        ItemList entries = itemGroupManager.getList(itemInHand);

        if (entries != null) {
            openScreen(new ItemListOverlay(entries));
            return true;
        } else {
            ItemGroup group = itemGroupManager.getItemPage(itemInHand);
            if (group != null) {
                openSquareSwitchScreen(group);
                return true;
            }
        }
        if (configManager.getConfig().fallbackInventory) {
            openInventoryScreen();
            return true;
        }
        return false;
    }

    public static void openInventoryScreen() {
        if (minecraft.screen instanceof SwitchItemOverlay overlay) {
            overlay.openInventory();
            return;
        }
        openScreen(SwitchItemOverlay.createInventoryOverlay());
    }

    public void openSquareSwitchScreen(ItemGroup group) {
        if (minecraft.screen instanceof SwitchItemOverlay overlay) {
            overlay.openItemGroup(group);
            return;
        }
        SwitchItemOverlay overlay = SwitchItemOverlay.createPaletteOverlay(group);
        openScreen(overlay);
        if (configManager.getConfig().startOnItem) {
            overlay.selectIcon("item|" + Item.getId(minecraft.player.getMainHandItem().getItem()), 0, 0);
        }
    }

    public void openPage(Page page) {
        if (minecraft.screen instanceof SwitchItemOverlay overlay) {
            overlay.openPage(page);
            return;
        }
        openScreen(SwitchItemOverlay.createPageOverlay(page));
    }

    /**
     * Opens a screen without unbinding the mouse
     * 
     * @param screen
     */
    private static void openScreen(Screen screen) {
        if (!AmecsAPISupport.getInstance().isActive()) {
            ((ExtendedMouseHandler) minecraft.mouseHandler).keepMouseGrabbed(true);
        }
        minecraft.setScreen(screen);
        minecraft.getSoundManager().resume();
        if (AmecsAPISupport.getInstance().isActive()) {
            minecraft.mouseHandler.grabMouse();
        } else {
            ((ExtendedMouseHandler) minecraft.mouseHandler).keepMouseGrabbed(false);
        }
    }

    public static void onPrimaryClick(@NotNull ItemSwapperUI xtOverlay, boolean forceClose) {
        boolean keepOpen = xtOverlay.onPrimaryClick();
        if (forceClose || !keepOpen) {
            minecraft.setScreen(null);
            if (!ConfigManager.getInstance().getConfig().allowWalkingWithUI) {
                KeyMapping.setAll();
            }
        }
    }

    public Screen createConfigScreen(Screen parent) {
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
                options.add(getOnOffOption("text.itemswapper.alwaysInventory", () -> configManager.getConfig().alwaysInventory,
                        b -> configManager.getConfig().alwaysInventory = b));
                options.add(getOnOffOption("text.itemswapper.showHotbar", () -> configManager.getConfig().showHotbar,
                        b -> configManager.getConfig().showHotbar = b));
                options.add(getOnOffOption("text.itemswapper.autoPalette", () -> configManager.getConfig().experimentalAutoPalette,
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

    public abstract void initModloader();

    public ItemGroupManager getItemGroupManager() {
        return itemGroupManager;
    }

    public void setEnableShulkers(boolean value) {
        this.enableShulkers = value;
    }

    public boolean areShulkersEnabled() {
        return this.enableShulkers && !configManager.getConfig().disableShulkers;
    }

    public boolean isEnableRefill() {
        return enableRefill && !configManager.getConfig().disableShulkers;
    }

    public void setEnableRefill(boolean enableRefill) {
        this.enableRefill = enableRefill;
    }

    public void setBypassExcepted(boolean bypassExcepted) {
        this.bypassAccepted = bypassExcepted;
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
                bypassAccepted = true;
                cacheManager.getCache().enableOnIp.add(server.ip);
            } else {
                cacheManager.getCache().disableOnIp.add(server.ip);
            }
            cacheManager.writeConfig();
            ItemSwapperSharedMod.LOGGER.info("Add {} to cached ip-addresses", server.ip);
        }
        minecraft.setScreen(null);
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

    public KeyMapping getKeybind() {
        return keybind;
    }

    public KeyMapping getInventoryKeybind() {
        return openInventoryKeybind;
    }

    public Item getLastItem() {
        return lastItem;
    }

    public void setLastItem(Item lastItem) {
        this.lastItem = lastItem;
    }

    public Page getLastPage() {
        return lastPage;
    }

    public void setLastPage(Page lastPage) {
        this.lastPage = lastPage;
    }

}

package dev.tr7zw.itemswapper;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.itemswapper.accessor.ExtendedMouseHandler;
import dev.tr7zw.itemswapper.compat.AmecsAPISupport;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.BlockTextureManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.provider.InstrumentItemNameProvider;
import dev.tr7zw.itemswapper.provider.PotionNameProvider;
import dev.tr7zw.itemswapper.provider.RecordNameProvider;
import dev.tr7zw.itemswapper.provider.ShulkerContainerProvider;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.item.Item;

public abstract class ItemSwapperSharedMod extends ItemSwapperBase {

    private static Minecraft minecraft = Minecraft.getInstance();

    public static ItemSwapperSharedMod instance;

    private final ItemGroupManager itemGroupManager = new ItemGroupManager();
    private final ClientProviderManager clientProviderManager = new ClientProviderManager();
    @Getter
    private final BlockTextureManager blockTextureManager = new BlockTextureManager();
    private final List<String> enableOnIp = cacheManager.getCache().enableOnIp;
    private final List<String> disableOnIp = cacheManager.getCache().disableOnIp;

    protected KeyMapping keybind = GeneralUtil.createKeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R,
            "itemswapper");
    protected KeyMapping openInventoryKeybind = GeneralUtil.createKeyMapping("key.itemswapper.openInventory",
            InputConstants.UNKNOWN.getValue(), "itemswapper");

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
        clientProviderManager.registerNameProvider(new InstrumentItemNameProvider());
        clientProviderManager.registerNameProvider(new RecordNameProvider());

        //#if MC < 12102
        //$$clientProviderManager.registerNameProvider(new dev.tr7zw.itemswapper.provider.SmithingTemplateItemNameProvider());
        //#endif
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
        if (minecraft.player != null && !itemGroupManager.isResourcepackSelected()
                && !configManager.getConfig().alwaysInventory) {
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
            ItemGroup group = itemGroupManager.getLastPickedItemGroup(itemInHand);
            if (group != null) {
                openSquareSwitchScreen(group);
                return true;
            }
            group = itemGroupManager.getItemPage(itemInHand);
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

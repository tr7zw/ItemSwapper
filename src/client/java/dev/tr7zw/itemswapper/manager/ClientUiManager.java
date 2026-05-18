package dev.tr7zw.itemswapper.manager;

import static dev.tr7zw.itemswapper.ItemSwapperSharedMod.LOGGER;

import com.mojang.blaze3d.platform.*;
import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.accessor.*;
import dev.tr7zw.itemswapper.compat.*;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.itemswapper.overlay.*;
import dev.tr7zw.transition.config.*;
import dev.tr7zw.transition.loader.*;
import dev.tr7zw.transition.mc.*;
import lombok.*;
import net.minecraft.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

@RequiredArgsConstructor
public class ClientUiManager {

    private static Minecraft minecraft = Minecraft.getInstance();
    private final ItemGroupManager itemGroupManager;
    private final ConfigManager<Config> configManager;
    private final ClientSessionSettings sessionSettings;
    private final ConfigManager<CacheServerAddresses> serverCache;

    @Getter
    protected KeyMapping keybind = GeneralUtil.createKeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R,
            "itemswapper");
    @Getter
    protected KeyMapping openInventoryKeybind = GeneralUtil.createKeyMapping("key.itemswapper.openInventory",
            InputConstants.UNKNOWN.getValue(), "itemswapper");

    private boolean pressed = false;

    public void initModloader() {
        ModLoaderUtil.registerKeybind(keybind);
        ModLoaderUtil.registerKeybind(openInventoryKeybind);
    }

    public void clientTick() {
        Screen screen = minecraft.screen;

        ServerData server = Minecraft.getInstance().getCurrentServer();

        if (server != null && serverCache.getConfig().disableOnIp.contains(server.ip)
                && !sessionSettings.isDisabledByPlayer()) {
            sessionSettings.setDisabledByPlayer(true);
            LOGGER.info("Itemswapper is deactivated for the server {}, because the player did not accept the warning!",
                    server.ip);
        } else if (keybind.isDown()) {
            if (sessionSettings.isModDisabled()) {
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
            if (sessionSettings.isModDisabled()) {
                minecraft.gui.setOverlayMessage(
                        ComponentProvider.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED),
                        false);
            } else if (screen == null) {
                openInventoryScreen();
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
            ClientUtil.sendActionBarMessage(ComponentProvider.translatable("text.itemswapper.resourcepack.notSelected")
                    .withStyle(ChatFormatting.RED));
        }

        if (!pressed && sessionSettings.isModDisabled()) {
            pressed = true;
            ClientUtil.sendActionBarMessage(
                    ComponentProvider.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED));
            return;
        }

        ServerData server = Minecraft.getInstance().getCurrentServer();
        if (!pressed) {
            if (sessionSettings.isDisabledByPlayer()) {
                ClientUtil.sendActionBarMessage(ComponentProvider.translatable("text.itemswapper.disabledByPlayer")
                        .withStyle(ChatFormatting.RED));
            } else if (server != null && !serverCache.getConfig().enableOnIp.contains(server.ip)
                    && !sessionSettings.isEnableShulkers() && !sessionSettings.isBypassAccepted()) {
                openConfirmationScreen();
            } else if (overlay == null) {
                if (!sessionSettings.isBypassAccepted() && server != null
                        && serverCache.getConfig().enableOnIp.contains(server.ip)) {
                    sessionSettings.setBypassAccepted(true);
                    ClientUtil.sendActionBarMessage(ComponentProvider.translatable("text.itemswapper.usedwhitelist")
                            .withStyle(ChatFormatting.GOLD));
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
            if (configManager.getConfig().listsAsPalette || entries.isPaletteList()) {
                openPage(new ItemGroupManager.ListPage(entries));
            } else {
                openScreen(new ItemListOverlay(entries));
            }
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

    public void openInventoryScreen() {
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

    public void openPage(ItemGroupManager.Page page) {
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
    private void openScreen(Screen screen) {
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

    public void onPrimaryClick(@NotNull ItemSwapperUI xtOverlay, boolean forceClose) {
        boolean keepOpen = xtOverlay.onPrimaryClick();
        if (forceClose || !keepOpen) {
            minecraft.setScreen(null);
            if (!ConfigHolder.getInstance().getGeneral().getConfig().allowWalkingWithUI) {
                KeyMapping.setAll();
            }
        }
    }

    private void acceptBypassCallback(boolean accepted) {
        ServerData server = Minecraft.getInstance().getCurrentServer();

        if (server != null) {
            if (accepted) {
                sessionSettings.setBypassAccepted(true);
                serverCache.getConfig().enableOnIp.add(server.ip);
            } else {
                serverCache.getConfig().disableOnIp.add(server.ip);
            }
            serverCache.writeConfig();
            ItemSwapperSharedMod.LOGGER.info("Add {} to cached ip-addresses", server.ip);
        }
        minecraft.setScreen(null);
    }

    public boolean areShulkersEnabled() {
        return sessionSettings.isEnableShulkers() && !configManager.getConfig().disableShulkers;
    }

    public boolean isEnableRefill() {
        return sessionSettings.isEnableRefill() && !configManager.getConfig().disableShulkers;
    }

}

package dev.tr7zw.itemswapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.manager.ItemGroupManager;
import dev.tr7zw.itemswapper.overlay.InventorySwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.RoundSwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.SquareSwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
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
    public static ItemSwapperSharedMod instance;
    private Minecraft minecraft = Minecraft.getInstance();
    private boolean enableShulkers = false;
    private boolean modDisabled = false;
    private boolean bypassExcepted = false;
    private ConfigManager configManager = ConfigManager.getInstance();
    private ItemGroupManager itemGroupManager = new ItemGroupManager();
    protected KeyMapping keybind = new KeyMapping("key.itemswapper.itemswitcher", InputConstants.KEY_R, "ItemSwapper");
    protected boolean pressed = false;

    public void init() {
        instance = this;
        LOGGER.info("Loading ItemSwapper!");

        initModloader();
    }

    public void clientTick() {
        Overlay overlay = Minecraft.getInstance().getOverlay();
        if (keybind.isDown()) {
            if (!pressed && isModDisabled()) {
                pressed = true;
                this.minecraft.gui.setOverlayMessage(
                        Component.translatable("text.itemswapper.disabled").withStyle(ChatFormatting.RED), false);
                return;
            }
            if(!pressed && !enableShulkers && !bypassExcepted) {
                pressed = true;
                this.minecraft.setScreen(new ConfirmScreen(accepted -> {
                    if(accepted) {
                        bypassExcepted = true;
                    }
                    this.minecraft.setScreen(null);
                }, Component.translatable("text.itemswapper.confirm.title"), Component.translatable("text.itemswapper.confirm.description")));
                return;
            }
            if (!pressed && overlay == null) {
                if(minecraft.player.getMainHandItem().isEmpty()) {
                    Minecraft.getInstance().setOverlay(new InventorySwitchItemOverlay());
                    pressed = true;
                    return;
                }
                Item itemInHand = minecraft.player.getMainHandItem().getItem();
                Item[] entries = itemGroupManager.getList(itemInHand);
                if (entries != null) {
                    Minecraft.getInstance().setOverlay(new ItemListOverlay(entries));
                    pressed = true;
                } else {
                    entries = itemGroupManager.getOpenList(itemInHand);
                    if (entries != null) {
                        openScreen(entries);

                        pressed = true;
                    }
                }
            } else if (!pressed && overlay instanceof XTOverlay xtOverlay) {
                xtOverlay.onClose();
                Minecraft.getInstance().setOverlay(null);
                pressed = true;
            }
        } else {
            pressed = false;
            if (!configManager.getConfig().toggleMode && overlay instanceof XTOverlay xtOverlay) {
                xtOverlay.onClose();
                Minecraft.getInstance().setOverlay(null);
            }
        }
    }
    
    public void openScreen(Item[] list) {
        if (configManager.getConfig().wipStyle == WIPStyle.HOLE) {
            Minecraft.getInstance().setOverlay(
                    new RoundSwitchItemOverlay(list));
        } else {
            Minecraft.getInstance().setOverlay(
                    new SquareSwitchItemOverlay(list));
        }
    }

    public Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.itemswapper.title") {

            @Override
            public void initialize() {
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getOnOffOption("text.itemswapper.toggleMode", () -> configManager.getConfig().toggleMode,
                        (b) -> configManager.getConfig().toggleMode = b));
                options.add(getOnOffOption("text.itemswapper.showCursor", () -> configManager.getConfig().showCursor,
                        (b) -> configManager.getConfig().showCursor = b));
                options.add(getEnumOption("text.itemswapper.wipstyle", WIPStyle.class,
                        () -> configManager.getConfig().wipStyle,
                        (b) -> configManager.getConfig().wipStyle = b));
                options.add(getOnOffOption("text.itemswapper.editMode", () -> configManager.getConfig().editMode,
                        (b) -> configManager.getConfig().editMode = b));
                options.add(getOnOffOption("text.itemswapper.creativeCheatMode",
                        () -> configManager.getConfig().creativeCheatMode,
                        (b) -> configManager.getConfig().creativeCheatMode = b));
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

}

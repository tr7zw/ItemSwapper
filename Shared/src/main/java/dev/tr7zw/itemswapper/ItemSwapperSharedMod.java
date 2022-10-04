package dev.tr7zw.itemswapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public abstract class ItemSwapperSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("ItemSwapper");
    public static ItemSwapperSharedMod instance;
    
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
            if (!pressed && overlay == null) {
                Item itemInHand = Minecraft.getInstance().player.getMainHandItem().getItem();
                if(itemInHand == Items.SPYGLASS) {
                    Minecraft.getInstance().setOverlay(new ItemListOverlay());
                }else {
                    Minecraft.getInstance().setOverlay(new SwitchItemOverlay(itemGroupManager.getSelection(itemInHand), itemGroupManager.getSecondarySelection(itemInHand)));
                }
            }
        } else {
            pressed = false;
            if(overlay instanceof XTOverlay xtOverlay) {
                xtOverlay.onClose();
                Minecraft.getInstance().setOverlay(null);
            }
        }
    }

    public abstract void initModloader();
    
}

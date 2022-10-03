package dev.tr7zw.xisumatweeks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.xisumatweeks.overlay.ItemListOverlay;
import dev.tr7zw.xisumatweeks.overlay.SwitchItemOverlay;
import dev.tr7zw.xisumatweeks.overlay.XTOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public abstract class XisumatweeksSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("Xisumatweeks");
    public static XisumatweeksSharedMod instance;
    
    private ItemGroupManager itemGroupManager = new ItemGroupManager();
    protected KeyMapping keybind = new KeyMapping("key.xisumatweeks.itemswitcher", InputConstants.KEY_R, "Xisumatweeks");
    protected boolean pressed = false;
    
    public void init() {
        instance = this;
        LOGGER.info("Loading Xisumatweeks!");
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

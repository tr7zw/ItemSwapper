package dev.tr7zw.xisumatweeks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;

public abstract class XisumatweeksSharedMod {

    public static final Logger LOGGER = LogManager.getLogger("Xisumatweeks");
    public static XisumatweeksSharedMod instance;
    
    protected KeyMapping keybind = new KeyMapping("key.xisumatweeks.itemswitcher", -1, "Xisumatweeks");
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
                Minecraft.getInstance().setOverlay(new SwitchItemOverlay());
                return;
            }
        } else {
            pressed = false;
            if(overlay instanceof SwitchItemOverlay switcher) {
                switcher.onClose();
                Minecraft.getInstance().setOverlay(null);
            }
        }
    }

    public abstract void initModloader();
    
}

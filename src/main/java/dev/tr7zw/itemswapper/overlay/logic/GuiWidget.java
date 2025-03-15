package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.client.gui.screens.Screen;

public interface GuiWidget {

    public List<GuiSlot> getSlots();

    public WidgetArea getWidgetArea();

    public void render(Screen parent, RenderContext graphics, int originX, int originY, boolean overwrideAvailable);

    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable,
            RenderContext graphics);

    public default void renderSelectedTooltip(SwitchItemOverlay overlay, RenderContext graphics, GuiSlot selected,
            double x, double y) {

    }

    /**
     * A click is done via the middle mouse key
     * 
     * @param slot
     */
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset);

    /**
     * Close is called when letting go from the key/re-pressing it in toggle
     * mode/left click
     * 
     * @param slot
     * @return should the ui stay open if possible
     */
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset);

    public default int titleYOffset() {
        return getWidgetArea().getBackgroundSizeY();
    }

    public default String getSelector(GuiSlot slot) {
        return null;
    }

}

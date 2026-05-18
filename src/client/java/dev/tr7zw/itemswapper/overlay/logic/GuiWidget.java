package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.client.gui.screens.Screen;

public interface GuiWidget {

    List<GuiSlot> getSlots();

    WidgetArea getWidgetArea();

    void render(Screen parent, RenderContext graphics, int originX, int originY, boolean overwrideAvailable);

    void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable,
            RenderContext graphics);

    default void renderSelectedTooltip(SwitchItemOverlay overlay, RenderContext graphics, GuiSlot selected, double x,
            double y) {

    }

    /**
     * A click is done via the middle mouse key
     * 
     * @param slot
     */
    void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset);

    /**
     * Close is called when letting go from the key/re-pressing it in toggle
     * mode/left click
     * 
     * @param slot
     * @return should the ui stay open if possible
     */
    boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset);

    default int titleYOffset() {
        return getWidgetArea().getBackgroundSizeY();
    }

    default String getSelector(GuiSlot slot) {
        return null;
    }

    default void remoteUpdate() {
        // default does nothing
    }

}

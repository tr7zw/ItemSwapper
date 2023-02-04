package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.client.gui.GuiComponent;

public interface GuiWidget {

    public List<GuiSlot> getSlots();

    public WidgetArea getWidgetArea();

    public void render(GuiComponent parent, PoseStack poseStack, int originX, int originY, boolean overwrideAvailable);

    public void renderSelectedSlotName(GuiSlot selected, int yOffset, boolean overwrideAvailable);

    /**
     * A click is done via the middle mouse key
     * 
     * @param slot
     */
    public void onClick(SwitchItemOverlay overlay, GuiSlot slot);

    /**
     * Close is called when letting go from the key/re-pressing it in toggle
     * mode/left click
     * 
     * @param slot
     */
    public void onClose(SwitchItemOverlay overlay, GuiSlot slot);

    public default int titleYOffset() {
        return getWidgetArea().getBackgroundSizeY();
    }

}

package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.itemswapper.compat.ControlifySupport;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUIAbstractInput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class GuiSelectionHandler {

    private final Minecraft minecraft = Minecraft.getInstance();
    private List<GuiWidget> widgets = new ArrayList<>();
    private GuiWidget selectedWidget = null;
    private GuiSlot selectedSlot = null;
    private double mouseX = 0;
    private double mouseY = 0;
    private double cursorX = 0;
    private double cursorY = 0;
    private double limitX = 5;
    private double limitY = 5;
    private double deadZone = 1;
    private double offsetX = 0;
    private double offsetY = 0;

    public void updateSelection(double x, double y) {
        if (ViveCraftSupport.getInstance().isActive() || ControlifySupport.getInstance().isActive()) {
            // Direct mouse input
            cursorX = mouseX;
            cursorY = mouseY;
        } else {
            cursorX += x;
            cursorY += y;
            cursorX = Mth.clamp(cursorX, -limitX, limitX);
            cursorY = Mth.clamp(cursorY, -limitY, limitY);
        }
        updateSelection();
    }

    public void updateMousePosition(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    private void updateSelection() {
        if (selectedSlot != null) {
            selectedSlot.selected().set(false);
            selectedSlot = null;
            selectedWidget = null;
        }
        double centerDist = Math.sqrt(cursorX * cursorX + cursorY * cursorY);
        if (centerDist < deadZone) {
            return;
        }
        double best = Double.MAX_VALUE;
        for (GuiWidget widget : widgets) {
            for (GuiSlot slot : widget.getSlots()) {
                int halfSlot = slot.size() / 2;
                // FIXME -3 to cancel out the bias. Might be that the cursor is not at the
                // correct position instead?
                double mouseDist = Math.sqrt((cursorX - 3 - slot.x() - widget.getWidgetArea().getX() - halfSlot)
                        * (cursorX - 3 - slot.x() - widget.getWidgetArea().getX() - halfSlot)
                        + (cursorY - 4 - slot.y() - widget.getWidgetArea().getY() - halfSlot)
                                * (cursorY - 4 - slot.y() - widget.getWidgetArea().getY() - halfSlot));
                double maxDistance = (slot.size() * Math.sqrt(2)) / 2;
                if (mouseDist < best && mouseDist < maxDistance) {
                    best = mouseDist;
                    selectedWidget = widget;
                    selectedSlot = slot;
                    offsetX = cursorX - 3 - slot.x() - widget.getWidgetArea().getX() - halfSlot;
                    offsetY = cursorY - 3 - slot.y() - widget.getWidgetArea().getY() - halfSlot;
                }
            }
        }
        if (selectedSlot != null) {
            selectedSlot.selected().set(true);
        }
    }

    public boolean select(String selector, int xOffset, int yOffset, ItemSwapperUIAbstractInput input) {
        for (GuiWidget widget : widgets) {
            for (GuiSlot slot : widget.getSlots()) {
                if (selector.equals(widget.getSelector(slot))) {
                    int halfSlot = slot.size() / 2;
                    this.cursorX = slot.x() + 3 + widget.getWidgetArea().getX() + halfSlot + xOffset;
                    this.cursorY = slot.y() + 3 + widget.getWidgetArea().getY() + halfSlot + yOffset;
                    input.handleMouseTeleport(
                            minecraft.getWindow().getWidth() / 2
                                    + (int) (cursorX * minecraft.getWindow().getGuiScale()),
                            minecraft.getWindow().getHeight() / 2
                                    + (int) (cursorY * minecraft.getWindow().getGuiScale()));
                    return true;
                }
            }
        }
        return false;
    }

    public GuiWidget getSelectedWidget() {
        return selectedWidget;
    }

    public GuiSlot getSelectedSlot() {
        return selectedSlot;
    }

    public void addWidget(GuiWidget widget) {
        this.widgets.add(widget);
        limitX = Math.max(limitX, widget.getWidgetArea().getMouseBoundsX() + widget.getWidgetArea().getX());
        limitY = Math.max(limitY, widget.getWidgetArea().getMouseBoundsY() + widget.getWidgetArea().getY());
    }

    public List<GuiWidget> getWidgets() {
        return this.widgets;
    }

    public void reset() {
        widgets.clear();
        selectedSlot = null;
        selectedWidget = null;
        limitX = 5;
        limitY = 5;
    }

    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }

    public void setLimitX(double limitX) {
        this.limitX = limitX;
    }

    public void setLimitY(double limitY) {
        this.limitY = limitY;
    }

    public void setDeadZone(double deadZone) {
        this.deadZone = deadZone;
    }

    public double getLimitX() {
        return limitX;
    }

    public double getLimitY() {
        return limitY;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

}

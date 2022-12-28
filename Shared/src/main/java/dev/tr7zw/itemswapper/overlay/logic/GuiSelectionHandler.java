package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;

public class GuiSelectionHandler {

    private List<GuiWidget> widgets = new ArrayList<>();
    private GuiWidget selectedWidget = null;
    private GuiSlot selectedSlot = null;
    private double cursorX = 0;
    private double cursorY = 0;
    private double limitX = 5;
    private double limitY = 5;
    private double deadZone = 1;
    
    public void updateSelection(double x, double y) {
        cursorX += x;
        cursorY += y;
        cursorX = Mth.clamp(cursorX, -limitX, limitX);
        cursorY = Mth.clamp(cursorY, -limitY, limitY);
        updateSelection();
    }
    
    private void updateSelection() {
        if(selectedSlot != null) {
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
            for(GuiSlot slot : widget.getSlots()) {
                int halfSlot = slot.size() / 2;
                // FIXME -3 to cancel out the bias. Might be that the cursor is not at the correct position instead?
                double mouseDist = Math.sqrt((cursorX - 3 - slot.x() - halfSlot) * (cursorX - 3 - slot.x() - halfSlot)
                        + (cursorY - 3 - slot.y() - halfSlot) * (cursorY - 3 - slot.y() - halfSlot));
                double maxDistance = (slot.size() * Math.sqrt(2))/2;
                if (mouseDist < best && mouseDist < maxDistance) {
                    best = mouseDist;
                    selectedWidget = widget;
                    selectedSlot = slot;
                }
            }
        }
        if(selectedSlot != null) {
            selectedSlot.selected().set(true);
        }
    }

    public GuiWidget getSelectedWidget() {
        return selectedWidget;
    }

    public GuiSlot getSelectedSlot() {
        return selectedSlot;
    }
    
    public void addWidget(GuiWidget widget) {
        this.widgets.add(widget);
        limitX = Math.max(limitX, widget.getMouseBoundsX() + widget.getX());
        limitY = Math.max(limitY, widget.getMouseBoundsY() + widget.getY());
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
    
}

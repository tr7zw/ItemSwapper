package dev.tr7zw.itemswapper.overlay.logic;

import java.util.concurrent.atomic.AtomicBoolean;

public record GuiSlot(int x, int y, int id, int size, AtomicBoolean selected) {

    public GuiSlot(int x, int y, int id, int size) {
        this(x, y, id, size, new AtomicBoolean());
    }

}

package dev.tr7zw.itemswapper.overlay;

import java.util.function.BiConsumer;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.ItemSwapperUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ItemSwapperUIAbstractInput extends Screen implements ItemSwapperUI {

    private BiConsumer<Integer, Integer> vCursorHandler = null;

    protected ItemSwapperUIAbstractInput(Component component) {
        super(component);
    }

    //? if >= 1.21.6 {

    @Override
    protected void renderBlurredBackground(net.minecraft.client.gui.GuiGraphics guiGraphics) {
        // No blur
    }
    //? }

    //? if >= 1.21.10 {

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.buttonInfo().button() == 0) {
            ItemSwapperSharedMod.onPrimaryClick(this, false);
        } else if (mouseButtonEvent.buttonInfo().button() == 1 || mouseButtonEvent.buttonInfo().button() == 2) {
            onSecondaryClick();
        }
        return true;
    }
    //? }

    //? if < 1.21.10 {
    /*
    @Override
    *///? }
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            ItemSwapperSharedMod.onPrimaryClick(this, false);
        } else if (i == 1 || i == 2) {
            onSecondaryClick();
        }
        return true;
    }

    public void registerVCursorHandler(BiConsumer<Integer, Integer> cons) {
        this.vCursorHandler = cons;
    }

    public void handleMouseTeleport(int x, int y) {
        if (vCursorHandler != null) {
            vCursorHandler.accept(x, y);
        }
    }

    public boolean hasVCursorHandler() {
        return vCursorHandler != null;
    }

    //? if >= 1.20.2 {

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        onScroll(g);
        return true;
    }
    //? } else {
    /*
    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        onScroll(f);
        return true;
    }
    *///? }

}

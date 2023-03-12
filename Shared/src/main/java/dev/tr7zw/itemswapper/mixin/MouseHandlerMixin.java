package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.Blaze3D;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private double lastMouseEventTime = Double.MIN_VALUE;
    @Shadow
    private double accumulatedDX;
    @Shadow
    private double accumulatedDY;

    private final ConfigManager configManager = ConfigManager.getInstance();
    private boolean middleIsPressed = false;
    private boolean leftIsPressed = false;

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    public void turnPlayer(CallbackInfo ci) {
        if (this.minecraft.getOverlay() instanceof ItemSwapperUI over && over.lockMouse()) {
            mouseHandler(over);
            ci.cancel();
        }
        if (this.minecraft.screen instanceof ItemSwapperUI over && over.lockMouse()) {
            mouseHandler(over);
            ci.cancel();
        }
    }

    private void mouseHandler(ItemSwapperUI over) {
        double d0 = Blaze3D.getTime();
        this.lastMouseEventTime = d0;
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            double d4 = 0.3D * configManager.getConfig().mouseSpeed;
            double d5 = d4 * d4 * d4;
            double d6 = d5 * 8.0D;
            double d2 = this.accumulatedDX * d6;
            double d3 = this.accumulatedDY * d6;

            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;

            if (this.minecraft.player != null) {
                over.handleInput(d2, d3);
            }
        } else {
            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;
        }
    }

    @Inject(method = "onPress", at = @At("TAIL"))
    private void onPress(long l, int i, int j, int k, CallbackInfo ci) {
        if (this.minecraft.getOverlay() instanceof ItemSwapperUI over) {
            onPress(i, over);
        } else if (this.minecraft.screen instanceof ItemSwapperUI over) {
            onPress(i, over);
        } else {
            middleIsPressed = false;
            leftIsPressed = false;
        }
    }

    private void onPress(int i, ItemSwapperUI over) {
        if (!leftIsPressed && i == 0) {
            over.close();
            leftIsPressed = true;
        } else {
            leftIsPressed = false;
        }
        if (!middleIsPressed && i == 2) {
            middleIsPressed = true;
            over.handleSwitchSelection();
        } else {
            middleIsPressed = false;
        }
    }

    @Redirect(method = "grabMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    public void grabMouse(Minecraft mc, Screen screen) {
        if (this.minecraft.screen instanceof ItemSwapperUI && screen == null) {
            // catch this call
        } else {
            mc.setScreen(screen);
        }
    }

    @Shadow
    public boolean isMouseGrabbed() {
        return false;
    }

}

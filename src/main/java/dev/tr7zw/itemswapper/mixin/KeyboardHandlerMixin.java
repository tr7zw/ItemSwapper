package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.InputConstants;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    private final ConfigManager configManager = ConfigManager.getInstance();

    @Inject(method = "keyPress", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        // restore movement, simulate "passEvents"
        if (this.minecraft.screen instanceof ItemSwapperUI) {
            InputConstants.Key key = InputConstants.getKey(i, j);
            if (!configManager.getConfig().allowWalkingWithUI
                    && !(ItemSwapperSharedMod.instance.getKeybind().matches(i, j)
                            || ItemSwapperSharedMod.instance.getInventoryKeybind().matches(i, j))) {
                return;
            }
            if (k == 0) {
                KeyMapping.set(key, false);
            } else {
                boolean bl2 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                if (bl2) {
                    KeyMapping.set(key, false);
                } else {
                    KeyMapping.set(key, true);
                    KeyMapping.click(key);
                }
            }
        }
    }

}

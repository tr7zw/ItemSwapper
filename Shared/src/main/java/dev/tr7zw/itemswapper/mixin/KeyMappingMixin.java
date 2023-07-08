package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
    private static void releaseAll(CallbackInfo ci) {
        if(Minecraft.getInstance().screen instanceof ItemSwapperUI) {
            ci.cancel();
        }
    }
    
}

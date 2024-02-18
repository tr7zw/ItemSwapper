package dev.tr7zw.itemswapper.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Shadow
    private static Map<String, KeyMapping> ALL;
    private static final ConfigManager configManager = ConfigManager.getInstance();

    @Inject(method = "releaseAll", at = @At("HEAD"), cancellable = true)
    private static void releaseAll(CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof ItemSwapperUI) {
            if (!configManager.getConfig().allowWalkingWithUI) {
                // stop walking now
                for (KeyMapping keyMapping : ALL.values())
                    if (!(keyMapping.same(ItemSwapperSharedMod.instance.getKeybind())
                            || keyMapping.same(ItemSwapperSharedMod.instance.getInventoryKeybind()))) {
                        keyMapping.setDown(false);
                    }
            }
            ci.cancel();
        }
    }

}

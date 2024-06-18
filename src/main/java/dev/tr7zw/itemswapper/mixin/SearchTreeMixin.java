package dev.tr7zw.itemswapper.mixin;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableResourceManager.class)
public class SearchTreeMixin {
    @Inject(method = "registerReloadListener", at = @At("TAIL"))
    private void registerReloadListenerMixin(PreparableReloadListener listener, CallbackInfo ci) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener(new SwapperResourceLoader());
    }
}
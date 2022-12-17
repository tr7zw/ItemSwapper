package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import dev.tr7zw.itemswapper.overlay.XTOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "runTick", at = @At(target = "Lnet/minecraft/client/server/IntegratedServer;isPublished()Z", value = "INVOKE", ordinal = 0))
    private boolean dontPauseSingleplayer(IntegratedServer server, boolean bl) {
        if (Minecraft.getInstance().getOverlay() instanceof XTOverlay) {
            return true;
        }
        return server.isPublished();
    }

    // FIXME
    @Inject(method = "createSearchTrees", at = @At("HEAD"))
    private void createSearchTrees(CallbackInfo ci) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener(new SwapperResourceLoader());
    }

}

package dev.tr7zw.xisumatweeks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.tr7zw.xisumatweeks.SwitchItemOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "runTick", at = @At( target = "Lnet/minecraft/client/server/IntegratedServer;isPublished()Z", value = "INVOKE", ordinal = 0))
    private boolean dontPauseSingleplayer(IntegratedServer server, boolean bl) {
        if(Minecraft.getInstance().getOverlay() instanceof SwitchItemOverlay) {
            return true;
        }
        return server.isPublished();
    }
    
}

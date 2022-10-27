package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import net.minecraft.client.multiplayer.ClientLevel;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo ci) {
        ItemSwapperSharedMod.instance.setEnableShulkers(false);
        ItemSwapperSharedMod.instance.setModDisabled(false);
    }
    
}

package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection arg, ServerPlayer arg2, CallbackInfo ci) {
        if (ConfigManager.getInstance().getConfig().serverPreventModUsage) {
            NetworkUtil.sendDisableModPacket(arg2, true);
        } else {
            NetworkUtil.sendShulkerSupportPacket(arg2, true);
            NetworkUtil.sendRefillSupportPacket(arg2, true);
        }
    }

}

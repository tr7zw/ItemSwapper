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

// spotless:off 
//#if MC >= 12002
import net.minecraft.server.network.CommonListenerCookie;
//#endif
//spotless:on

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    // spotless:off 
  //#if MC >= 12002
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer,
            CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        //#else
        //$$ public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        //#endif
        //spotless:on
        if (ConfigManager.getInstance().getConfig().serverPreventModUsage) {
            NetworkUtil.sendDisableModPacket(serverPlayer, true);
        } else {
            NetworkUtil.sendShulkerSupportPacket(serverPlayer, true);
            NetworkUtil.sendRefillSupportPacket(serverPlayer, true);
        }
    }

}

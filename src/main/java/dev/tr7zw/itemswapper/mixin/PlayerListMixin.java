package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import dev.tr7zw.itemswapper.util.ServerNetworkUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

//#if MC >= 12002
import net.minecraft.server.network.CommonListenerCookie;
//#endif

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    //#if MC >= 12002
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer,
            CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        //#else
        //$$ public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        //#endif
        if (ConfigManager.getInstance().getConfig().serverPreventModUsage) {
            ServerNetworkUtil.sendDisableModPacket(serverPlayer, true);
        } else {
            ServerNetworkUtil.sendShulkerSupportPacket(serverPlayer, true);
            ServerNetworkUtil.sendRefillSupportPacket(serverPlayer, true);
        }
    }

}

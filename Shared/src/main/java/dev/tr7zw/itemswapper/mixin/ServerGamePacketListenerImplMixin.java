package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.server.ItemSwapperSharedServer;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    private static final ConfigManager configManager = ConfigManager.getInstance();

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        // Don't apply this logic, if the server has the mod disabled.
        if (NetworkUtil.swapMessage.equals(serverboundCustomPayloadPacket.getIdentifier())
                && !configManager.getConfig().serverPreventModUsage) {
            ItemSwapperSharedServer.INSTANCE.getItemHandler().swapItem(player, serverboundCustomPayloadPacket);
        }
        if (NetworkUtil.refillMessage.equals(serverboundCustomPayloadPacket.getIdentifier())
                && !configManager.getConfig().serverPreventModUsage) {
            ItemSwapperSharedServer.INSTANCE.getItemHandler().refillSlot(player, serverboundCustomPayloadPacket);
        }
    }

}

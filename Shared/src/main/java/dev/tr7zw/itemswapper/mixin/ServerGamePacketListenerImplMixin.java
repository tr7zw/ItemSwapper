package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import dev.tr7zw.itemswapper.server.ItemSwapperSharedServer;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    private static final ConfigManager configManager = ConfigManager.getInstance();

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        try {
            if ((Object) this instanceof ServerGamePacketListenerImpl gamePacketListener) {
                // Don't apply this logic, if the server has the mod disabled.
                if (!configManager.getConfig().serverPreventModUsage
                        && serverboundCustomPayloadPacket.payload() instanceof SwapItemPayload bytebuf) {
                    ItemSwapperSharedServer.INSTANCE.getItemHandler().swapItem(gamePacketListener.player, bytebuf);
                }
                if (!configManager.getConfig().serverPreventModUsage
                        && serverboundCustomPayloadPacket.payload() instanceof RefillItemPayload bytebuf) {
                    ItemSwapperSharedServer.INSTANCE.getItemHandler().refillSlot(gamePacketListener.player, bytebuf);
                }
            }
        } catch (Throwable th) {
            ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
        }
    }

}

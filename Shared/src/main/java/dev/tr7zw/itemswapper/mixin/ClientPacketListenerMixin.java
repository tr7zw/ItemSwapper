package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
        if(NetworkUtil.enableShulkerMessage.equals(clientboundCustomPayloadPacket.getIdentifier())) {
            try {
                ItemSwapperSharedMod.instance.setEnableShulkers(clientboundCustomPayloadPacket.getData().readBoolean());
            }catch(Throwable th) {
                ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
            }
        }
        if(NetworkUtil.disableModMessage.equals(clientboundCustomPayloadPacket.getIdentifier())) {
            try {
                ItemSwapperSharedMod.instance.setModDisabled(clientboundCustomPayloadPacket.getData().readBoolean());
            }catch(Throwable th) {
                ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
            }
        }
    }
    
    
}

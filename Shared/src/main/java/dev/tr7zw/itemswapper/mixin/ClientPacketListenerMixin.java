package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
        ItemSwapperSharedMod.LOGGER.info("Client packet " + clientboundCustomPayloadPacket.getIdentifier() + " " + clientboundCustomPayloadPacket.getData().toString());
    }
    
    
}

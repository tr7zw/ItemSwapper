package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(CustomPacketPayload customPacketPayload, CallbackInfo ci) {
        if(customPacketPayload instanceof PacketByteBufPayload payload) {
            if (NetworkUtil.enableShulkerMessage.equals(payload.id())) {
                try {
                    ItemSwapperSharedMod.instance.setEnableShulkers(payload.data().readBoolean());
                } catch (Throwable th) {
                    ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                }
            }
            if (NetworkUtil.disableModMessage.equals(payload.id())) {
                try {
                    ItemSwapperSharedMod.instance.setModDisabled(payload.data().readBoolean());
                } catch (Throwable th) {
                    ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                }
            }
            if (NetworkUtil.enableRefillMessage.equals(payload.id())) {
                try {
                    ItemSwapperSharedMod.instance.setEnableRefill(payload.data().readBoolean());
                } catch (Throwable th) {
                    ItemSwapperSharedMod.LOGGER.error("Error while processing packet!", th);
                }
            }
        }
    }

}

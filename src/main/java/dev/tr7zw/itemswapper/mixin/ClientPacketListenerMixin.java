package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.ItemSwapperBase;
import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import net.minecraft.client.multiplayer.ClientPacketListener;

//spotless:off 
//#if MC >= 12002
 import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//$$ import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//$$ import dev.tr7zw.itemswapper.accessor.CustomPayloadLoader;
//#endif
//spotless:on

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    // spotless:off 
  //#if MC >= 12002
    public void handleCustomPayload(CustomPacketPayload customPacketPayload, CallbackInfo ci) {
        try {
      //#else
      //$$     public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket, CallbackInfo ci) {
      //$$ try {
      //$$ CustomPacketPayload customPacketPayload = ((CustomPayloadLoader)clientboundCustomPayloadPacket).resolveObject(clientboundCustomPayloadPacket.getIdentifier(), clientboundCustomPayloadPacket.getData());
      //#endif
      //spotless:on

            if (customPacketPayload instanceof ShulkerSupportPayload payload) {
                ItemSwapperSharedMod.instance.setEnableShulkers(payload.enabled());
            }
            if (customPacketPayload instanceof DisableModPayload payload) {
                ItemSwapperSharedMod.instance.setModDisabled(payload.enabled());
            }
            if (customPacketPayload instanceof RefillSupportPayload payload) {
                ItemSwapperSharedMod.instance.setEnableRefill(payload.enabled());
            }
        } catch (Throwable th) {
            ItemSwapperBase.LOGGER.error("Error while processing packet!", th);
        }
    }

}

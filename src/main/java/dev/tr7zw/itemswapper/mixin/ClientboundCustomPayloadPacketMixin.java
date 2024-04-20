package dev.tr7zw.itemswapper.mixin;

//spotless:off 
//#if MC >= 12005
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;

@Mixin(ClientboundCustomPayloadPacket.class)
public class ClientboundCustomPayloadPacketMixin {

}
//#else
//$$
//$$ import java.util.HashMap;
//$$ import java.util.Map;
//$$
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ import dev.tr7zw.itemswapper.packets.DisableModPayload;
//$$ import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
//$$ import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import dev.tr7zw.itemswapper.accessor.CustomPayloadLoader;
//$$
//$$ //spotless:off 
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//#endif
//$$ //spotless:on
//$$
//$$ @Mixin(ClientboundCustomPayloadPacket.class)
//$$ public class ClientboundCustomPayloadPacketMixin implements CustomPayloadLoader {
//$$
//$$     private static final Map<ResourceLocation, FriendlyByteBuf.Reader<? extends CustomPacketPayload>> ITEMSWAPPER_PACKETS = new HashMap<>() {
//$$         private static final long serialVersionUID = 1L;
//$$
//$$         {
//$$             put(ShulkerSupportPayload.ID, ShulkerSupportPayload::new);
//$$             put(DisableModPayload.ID, DisableModPayload::new);
//$$             put(RefillSupportPayload.ID, RefillSupportPayload::new);
//$$         }
//$$     };
//$$
//$$     // spotless:off 
  //#if MC >= 12002
  //$$   @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
  //$$   private static void readPayload(ResourceLocation id, FriendlyByteBuf buffer,
  //$$           CallbackInfoReturnable<CustomPacketPayload> ci) {
  //$$       FriendlyByteBuf.Reader<? extends CustomPacketPayload> reader = ITEMSWAPPER_PACKETS.get(id);
  //$$       if (reader != null) {
  //$$           ci.setReturnValue(reader.apply(buffer));
  //$$           ci.cancel();
  //$$       }
  //$$   }
  //#endif
//$$   //spotless:on
//$$
//$$     public CustomPacketPayload resolveObject(ResourceLocation id, FriendlyByteBuf buffer) {
//$$         FriendlyByteBuf.Reader<? extends CustomPacketPayload> reader = ITEMSWAPPER_PACKETS.get(id);
//$$         if (reader != null) {
//$$             return reader.apply(buffer);
//$$         }
//$$         return null;
//$$     }
//$$
//$$ }
//#endif
//spotless:on
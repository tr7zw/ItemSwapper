package dev.tr7zw.itemswapper.mixin;

//#if MC >= 12005
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

@Mixin(ServerboundCustomPayloadPacket.class)
public class ServerboundCustomPayloadPacketMixin {

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
//$$ import dev.tr7zw.itemswapper.packets.RefillItemPayload;
//$$ import dev.tr7zw.itemswapper.packets.SwapItemPayload;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
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
//$$ @Mixin(ServerboundCustomPayloadPacket.class)
//$$ public class ServerboundCustomPayloadPacketMixin implements CustomPayloadLoader {
//$$
//$$     private static final Map<ResourceLocation, FriendlyByteBuf.Reader<? extends CustomPacketPayload>> ITEMSWAPPER_PACKETS = new HashMap<>() {
//$$         private static final long serialVersionUID = 1L;
//$$
//$$         {
//$$             put(SwapItemPayload.ID, SwapItemPayload::new);
//$$             put(RefillItemPayload.ID, RefillItemPayload::new);
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
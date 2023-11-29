package dev.tr7zw.itemswapper.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr7zw.itemswapper.packets.DisableModPayload;
import dev.tr7zw.itemswapper.packets.RefillSupportPayload;
import dev.tr7zw.itemswapper.packets.ShulkerSupportPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@Mixin(ClientboundCustomPayloadPacket.class)
public class ClientboundCustomPayloadPacketMixin {

    private static final Map<ResourceLocation, FriendlyByteBuf.Reader<? extends CustomPacketPayload>> ITEMSWAPPER_PACKETS = new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put(ShulkerSupportPayload.ID, ShulkerSupportPayload::new);
            put(DisableModPayload.ID, DisableModPayload::new);
            put(RefillSupportPayload.ID, RefillSupportPayload::new);
        }
    };

    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void readPayload(ResourceLocation id, FriendlyByteBuf buffer,
            CallbackInfoReturnable<CustomPacketPayload> ci) {
        FriendlyByteBuf.Reader<? extends CustomPacketPayload> reader = ITEMSWAPPER_PACKETS.get(id);
        if (reader != null) {
                ci.setReturnValue(reader.apply(buffer));
        }
    }

}

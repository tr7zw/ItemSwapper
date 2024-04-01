package dev.tr7zw.itemswapper.accessor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

//spotless:off 
//#if MC >= 12002
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//#endif
//spotless:on

public interface CustomPayloadLoader {

    CustomPacketPayload resolveObject(ResourceLocation id, FriendlyByteBuf buffer);

}

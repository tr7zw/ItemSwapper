package dev.tr7zw.itemswapper.accessor;

//#if MC >= 12005
//#elseif MC >= 12002
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.resources.ResourceLocation;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.resources.ResourceLocation;
//#endif

public interface CustomPayloadLoader {

    //#if MC < 12005
    //$$  CustomPacketPayload resolveObject(ResourceLocation id, FriendlyByteBuf buffer);
    //#endif

}
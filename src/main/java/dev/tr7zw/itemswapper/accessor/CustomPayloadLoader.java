package dev.tr7zw.itemswapper.accessor;

//? if >= 1.20.5 {

//? } else if >= 1.20.2 {
/*
 import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
 import net.minecraft.network.FriendlyByteBuf;
 import net.minecraft.resources.ResourceLocation;
*///? } else {
/*
import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
*///? }

public interface CustomPayloadLoader {

    //? if < 1.20.5 {
/*
    CustomPacketPayload resolveObject(ResourceLocation id, FriendlyByteBuf buffer);
    *///? }

}

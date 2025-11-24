package dev.tr7zw.itemswapper.packets;

//? if >= 1.20.5 {

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.*;
//? }

//? if >= 1.20.5 {

public interface CustomPacketPayloadSupport extends CustomPacketPayload {
    //? } else {
    /*
    public interface CustomPacketPayloadSupport {
    *///? }

    //? if >= 1.20.5 {

    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id();

    public default Type<? extends CustomPacketPayload> type() {
        return new Type<CustomPacketPayload>(id());
    }

    public void write(FriendlyByteBuf paramFriendlyByteBuf);

    //? }

}

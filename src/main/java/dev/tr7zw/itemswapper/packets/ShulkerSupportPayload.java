package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.util.ServerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.*;

//? if >= 1.20.2 {

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? } else {
/*
import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
*///? }

public record ShulkerSupportPayload(boolean enabled) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ ID = ServerUtil
            .getResourceLocation(ItemSwapperMod.MODID, "enableshulker");

    @Override
    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeBoolean(enabled);
    }

    public ShulkerSupportPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}

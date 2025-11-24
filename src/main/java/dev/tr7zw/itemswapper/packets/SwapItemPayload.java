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

public record SwapItemPayload(int inventorySlot, int slot) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ ID = ServerUtil
            .getResourceLocation(ItemSwapperMod.MODID, "swap");

    @Override
    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(inventorySlot);
        paramFriendlyByteBuf.writeInt(slot);
    }

    public SwapItemPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

}

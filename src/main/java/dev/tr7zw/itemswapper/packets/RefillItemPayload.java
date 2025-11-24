package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.util.ServerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

//? if >= 1.20.2 {

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

//? } else {
/*
import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
*///? }

public record RefillItemPayload(int slot) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final ResourceLocation ID = ServerUtil.getResourceLocation(ItemSwapperMod.MODID, "refill");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(slot);
    }

    public RefillItemPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

}

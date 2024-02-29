package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DisableModPayload(boolean enabled) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ItemSwapperMod.MODID, "disable");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeBoolean(enabled);
    }

    public DisableModPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}
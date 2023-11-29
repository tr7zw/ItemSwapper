package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwapItemPayload(int inventorySlot, int slot) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ItemSwapperMod.MODID, "swap");
    
    @Override
    public ResourceLocation id() {
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

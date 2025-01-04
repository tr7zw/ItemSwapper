package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.util.ServerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

//#if MC >= 12002
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//#endif

public record SwapItemPayload(int inventorySlot, int slot) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final ResourceLocation ID = ServerUtil.getResourceLocation(ItemSwapperMod.MODID, "swap");

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

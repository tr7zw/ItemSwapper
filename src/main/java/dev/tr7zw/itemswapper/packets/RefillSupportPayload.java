package dev.tr7zw.itemswapper.packets;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperMod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

//#if MC >= 12002
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

//#else
//$$ import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
//#endif

public record RefillSupportPayload(boolean enabled) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final ResourceLocation ID = getResourceLocation(ItemSwapperMod.MODID, "enablerefill");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeBoolean(enabled);
    }

    public RefillSupportPayload(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

}

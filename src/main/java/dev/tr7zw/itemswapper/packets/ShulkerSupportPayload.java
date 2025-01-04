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

public record ShulkerSupportPayload(boolean enabled) implements CustomPacketPayload, CustomPacketPayloadSupport {

    public static final ResourceLocation ID = ServerUtil.getResourceLocation(ItemSwapperMod.MODID, "enableshulker");

    @Override
    public ResourceLocation id() {
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

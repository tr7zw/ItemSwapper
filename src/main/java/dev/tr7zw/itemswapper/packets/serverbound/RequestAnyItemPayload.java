package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

public record RequestAnyItemPayload(Item item, EmptySlotPayload emptySlotPayload)
        implements CustomPacketPayloadSupport {

    public static final RequestAnyItemPayload INSTANCE = new RequestAnyItemPayload(Items.AIR, null);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "request_any_item").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeUtf(item.toString());
        emptySlotPayload.write(paramFriendlyByteBuf);
    }

    @Override
    public RequestAnyItemPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new RequestAnyItemPayload(friendlyByteBuf);
    }

    public RequestAnyItemPayload(FriendlyByteBuf buffer) {
        this(ItemUtil.getItem(McId.create(buffer.readUtf()).id()), EmptySlotPayload.INSTANCE.read(buffer));
    }

}

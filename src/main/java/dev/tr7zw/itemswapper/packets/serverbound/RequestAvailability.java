package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

public record RequestAvailability(ItemListing itemListing) implements CustomPacketPayloadSupport {

    public static final RequestAvailability INSTANCE = new RequestAvailability(ItemListing.of(Items.AIR));
    public static final Identifier ID = McId.create(ItemSwapperMod.MODID, "request_availability").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        itemListing.write(paramFriendlyByteBuf);
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new RequestAvailability(friendlyByteBuf);
    }

    public RequestAvailability(FriendlyByteBuf buffer) {
        this(ItemListing.parse(buffer));
    }

}

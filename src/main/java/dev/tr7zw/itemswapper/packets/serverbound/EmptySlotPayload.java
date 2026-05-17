package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

/**
 * Payload for trying to store away an item in the inventory.
 * 
 * @param slot Inventory slot id
 */
public record EmptySlotPayload(int slot, ItemListing itemListing) implements CustomPacketPayloadSupport {

    public static final EmptySlotPayload INSTANCE = new EmptySlotPayload(0, null);
    public static final Identifier ID = McId.create(ItemSwapperMod.MODID, "empty_slot").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(slot);
        itemListing.write(paramFriendlyByteBuf);
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new EmptySlotPayload(friendlyByteBuf);
    }

    public EmptySlotPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), ItemListing.parse(buffer));
    }

}

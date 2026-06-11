package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.*;

/**
 * Payload for swapping items in the inventory.
 * 
 * @param inventorySlot The inventory slot of the shulker box.
 * @param slot          Slot inside the shulkerbox to swap with the currently
 *                      selected item.
 */
@Deprecated
public record SwapItemPayload(int inventorySlot, int slot) implements CustomPacketPayloadSupport {

    public static final SwapItemPayload INSTANCE = new SwapItemPayload(0, 0);
    public static final Identifier ID = McId.create(ItemSwapperBase.MODID, "swap").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(inventorySlot);
        paramFriendlyByteBuf.writeInt(slot);
    }

    @Override
    public SwapItemPayload read(FriendlyByteBuf friendlyByteBuf) {
        return new SwapItemPayload(friendlyByteBuf);
    }

    public SwapItemPayload(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt());
    }

}

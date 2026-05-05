package dev.tr7zw.itemswapper.packets.clientbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;

import java.util.*;

public record ItemAvailability(List<RemoteItem> items) implements CustomPacketPayloadSupport {

    public static final ItemAvailability INSTANCE = new ItemAvailability(Collections.emptyList());
    public static final Identifier ID = McId.create(ItemSwapperMod.MODID, "item_availability").id();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(items.size());
        for (RemoteItem item : items) {
            item.write(paramFriendlyByteBuf);
        }
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new ItemAvailability(friendlyByteBuf);
    }

    public ItemAvailability(FriendlyByteBuf buffer) {
        this(parseItems(buffer));
    }

    private static List<RemoteItem> parseItems(FriendlyByteBuf buffer) {
        List<RemoteItem> items = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            items.add(new RemoteItem(buffer));
        }
        return items;
    }

}

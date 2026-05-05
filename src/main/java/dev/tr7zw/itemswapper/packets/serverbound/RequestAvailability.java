package dev.tr7zw.itemswapper.packets.serverbound;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

import java.util.*;

public record RequestAvailability(List<String> items) implements CustomPacketPayloadSupport {

    public static final RequestAvailability INSTANCE = new RequestAvailability(Collections.emptyList());
    public static final Identifier ID = McId.create(ItemSwapperMod.MODID, "request_availability").id();

    @Override
    public Identifier id() {
        return ID;
    }

    public RequestAvailability(Item... items) {
        this(Arrays.asList(items).stream().map(Item::toString).toList());
    }

    public RequestAvailability(ItemEntry... items) {
        this(Arrays.asList(items).stream().map(ItemEntry::getItem).map(Item::toString).toList());
    }

    @Override
    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeInt(items.size());
        for (String item : items) {
            paramFriendlyByteBuf.writeUtf(item);
        }
    }

    @Override
    public CustomPacketPayloadSupport read(FriendlyByteBuf friendlyByteBuf) {
        return new RequestAvailability(friendlyByteBuf);
    }

    public RequestAvailability(FriendlyByteBuf buffer) {
        this(parseItems(buffer));
    }

    private static List<String> parseItems(FriendlyByteBuf buffer) {
        List<String> items = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            items.add(buffer.readUtf());
        }
        return items;
    }

}

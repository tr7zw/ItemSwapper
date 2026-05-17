package dev.tr7zw.itemswapper.packets;

import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.network.*;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.stream.*;

public record ItemListing(List<String> items) {

    private final static byte VERSION = 0;

    public static ItemListing of(Item... items) {
        return new ItemListing(Arrays.asList(items).stream().map(Item::toString).toList());
    }

    public static ItemListing of(ItemEntry... items) {
        return new ItemListing(Arrays.asList(items).stream().map(ItemEntry::getItem).map(Item::toString).toList());
    }

    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeByte(VERSION);
        paramFriendlyByteBuf.writeInt(items.size());
        for (String item : items) {
            paramFriendlyByteBuf.writeUtf(item);
        }
    }

    public Set<Item> asItemSet() {
        return items().stream().map(s -> ItemUtil.getItem(McId.create(s).id())).collect(Collectors.toSet());
    }

    public static ItemListing parse(FriendlyByteBuf buffer) {
        byte version = buffer.readByte();
        if (version != VERSION) {
            throw new RuntimeException("Unsupported version: " + version);
        }
        List<String> items = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            items.add(buffer.readUtf());
        }
        return new ItemListing(items);
    }

}

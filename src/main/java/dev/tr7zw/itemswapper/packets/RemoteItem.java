package dev.tr7zw.itemswapper.packets;

import com.mojang.brigadier.exceptions.*;
import com.mojang.serialization.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.world.item.*;

import java.util.*;

public record RemoteItem(String providerId, ItemStack itemStack, int slot, int id, int count) {

    private final static byte VERSION = 0;

    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeByte(VERSION);
        paramFriendlyByteBuf.writeUtf(providerId);
        paramFriendlyByteBuf.writeUtf(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, itemStack).getOrThrow().toString());
        paramFriendlyByteBuf.writeInt(slot);
        paramFriendlyByteBuf.writeInt(id);
        paramFriendlyByteBuf.writeInt(count);
    }

    public static RemoteItem parse(FriendlyByteBuf buffer) {
        byte version = buffer.readByte();
        if (version != VERSION) {
            throw new RuntimeException("Unsupported version: " + version);
        }
        return new RemoteItem(buffer.readUtf(), decodeItemStack(buffer.readUtf()), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static List<RemoteItem> parseList(FriendlyByteBuf buffer) {
        List<RemoteItem> items = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            items.add(RemoteItem.parse(buffer));
        }
        return items;
    }

    public static void writeList(FriendlyByteBuf buffer, List<RemoteItem> items) {
        buffer.writeInt(items.size());
        for (RemoteItem item : items) {
            item.write(buffer);
        }
    }

    private static ItemStack decodeItemStack(String nbtString) {
        try {
            return ItemStack.CODEC.decode(NbtOps.INSTANCE, TagParser.parseCompoundFully(nbtString)).getOrThrow()
                    .getFirst();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}

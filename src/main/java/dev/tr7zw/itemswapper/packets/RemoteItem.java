package dev.tr7zw.itemswapper.packets;

import com.mojang.brigadier.exceptions.*;
import dev.tr7zw.itemswapper.util.*;
import dev.tr7zw.transition.mc.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.world.item.*;

import java.util.*;

public record RemoteItem(String providerId, ItemStack itemStack, int slot, int id, int count) {

    private final static byte VERSION = 0;

    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeByte(VERSION);
        paramFriendlyByteBuf.writeUtf(providerId);
        // ItemStack.CODEC refuses air. Container grids include empty slots.
        if (itemStack.isEmpty()) {
            paramFriendlyByteBuf.writeUtf("");
        } else {
            paramFriendlyByteBuf.writeUtf(ItemUtil.encodeItemStack(LevelProvider.getLevel(), itemStack));
        }
        paramFriendlyByteBuf.writeInt(slot);
        paramFriendlyByteBuf.writeInt(id);
        paramFriendlyByteBuf.writeInt(count);
    }

    public static RemoteItem parse(FriendlyByteBuf buffer) {
        byte version = buffer.readByte();
        if (version != VERSION) {
            throw new RuntimeException("Unsupported version: " + version);
        }
        String providerId = buffer.readUtf();
        String encoded = buffer.readUtf();
        ItemStack stack = encoded.isEmpty() ? ItemStack.EMPTY
                : ItemUtil.decodeItemStack(LevelProvider.getLevel(), encoded);
        return new RemoteItem(providerId, stack, buffer.readInt(), buffer.readInt(), buffer.readInt());
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

    // needed due to Mojang renaming getCount to count in 26.1, makes things simpler
    public int getCount() {
        return count;
    }

}

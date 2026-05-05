package dev.tr7zw.itemswapper.packets;

import com.mojang.brigadier.exceptions.*;
import com.mojang.serialization.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.world.item.*;

public record RemoteItem(String providerId, ItemStack itemStack, int slot, int id, int count) {

    public void write(FriendlyByteBuf paramFriendlyByteBuf) {
        paramFriendlyByteBuf.writeUtf(providerId);
        paramFriendlyByteBuf.writeUtf(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, itemStack).getOrThrow().toString());
        paramFriendlyByteBuf.writeInt(slot);
        paramFriendlyByteBuf.writeInt(id);
        paramFriendlyByteBuf.writeInt(count);
    }

    public RemoteItem(FriendlyByteBuf buffer) {
        this(buffer.readUtf(), decodeItemStack(buffer.readUtf()), buffer.readInt(), buffer.readInt(), buffer.readInt());
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

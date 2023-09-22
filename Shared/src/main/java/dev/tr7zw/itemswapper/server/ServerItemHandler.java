package dev.tr7zw.itemswapper.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ServerItemHandler {

    private static final Logger network_logger = LogManager.getLogger("ItemSwapper-Network");

    public void swapItem(ServerPlayer player, PacketByteBufPayload serverboundCustomPayloadPacket) {
        try {
            FriendlyByteBuf buf = serverboundCustomPayloadPacket.data();
            int inventory = buf.readInt();
            int slot = buf.readInt();
            if (ShulkerHelper.isShulker(player.getInventory().getSelected().getItem())) {
                // Don't try to put a shulker into another shulker
                return;
            }
            ItemStack shulker = player.getInventory().items.get(inventory);
            NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
            if (content != null) {
                ItemStack tmp = content.get(slot);
                content.set(slot, player.getInventory().getSelected());
                player.getInventory().setItem(player.getInventory().selected, tmp);
                ShulkerHelper.setItem(shulker, content);
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    public void refillSlot(ServerPlayer player, PacketByteBufPayload serverboundCustomPayloadPacket) {
        try {
            FriendlyByteBuf buf = serverboundCustomPayloadPacket.data();
            int targetSlot = buf.readInt();
            ItemStack target = player.getInventory().getItem(targetSlot);
            if (target == null || target.isEmpty()) {
                return;
            }
            int space = target.getMaxStackSize() - target.getCount();
            if (space <= 0) {
                // nothing to do
                return;
            }
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack shulker = player.getInventory().items.get(i);
                NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
                if (content != null) {
                    boolean boxChanged = false;
                    for (int entry = 0; entry < content.size(); entry++) {
                        ItemStack boxItem = content.get(entry);
                        if (ItemStack.isSameItemSameTags(boxItem, target)) {
                            // same, use to restock
                            int amount = Math.min(space, boxItem.getCount());
                            target.setCount(target.getCount() + amount);
                            boxItem.setCount(boxItem.getCount() - amount);
                            space -= amount;
                            boxChanged = true;
                            if (space <= 0) {
                                break;
                            }
                        }
                    }
                    if (boxChanged) {
                        ShulkerHelper.setItem(shulker, content);
                    }
                }
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

}

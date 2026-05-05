package dev.tr7zw.itemswapper.server.manger;

import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.packets.clientbound.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.config.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
public class ServerItemHandler {

    private static final Logger network_logger = LogManager.getLogger("ItemSwapper-Network");
    private static final ConfigManager<Config> configManager = ConfigHolder.getInstance().getGeneral();
    private final ServerProviderManager providerManager;

    public void swapItem(ServerPlayer player, SwapItemPayload payload) {
        if (configManager.getConfig().disableShulkers) {
            // no refill allowed
            return;
        }
        try {
            if (ShulkerHelper.isShulker(InventoryUtil.getSelected(player.getInventory()).getItem())) {
                // Don't try to put a shulker into another shulker
                return;
            }
            ItemStack shulker = player.getInventory().getItem(payload.inventorySlot());
            NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
            if (content != null) {
                ItemStack tmp = content.get(payload.slot());
                content.set(payload.slot(), InventoryUtil.getSelected(player.getInventory()));
                player.getInventory().setItem(InventoryUtil.getSelectedId(player.getInventory()), tmp);
                ShulkerHelper.setItem(shulker, content);
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    public void refillSlot(ServerPlayer player, RefillItemPayload payload) {
        if (configManager.getConfig().disableShulkers) {
            // no refill allowed
            return;
        }
        try {
            ItemStack target = player.getInventory().getItem(payload.slot());
            if (target == null || target.isEmpty()) {
                return;
            }
            int space = target.getMaxStackSize() - target.getCount();
            if (space <= 0) {
                // nothing to do
                return;
            }
            for (int i = 0; i < InventoryUtil.getNonEquipmentItems(player.getInventory()).size(); i++) {
                ItemStack shulker = InventoryUtil.getNonEquipmentItems(player.getInventory()).get(i);
                NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
                if (content != null) {
                    boolean boxChanged = false;
                    for (int entry = 0; entry < content.size(); entry++) {
                        ItemStack boxItem = content.get(entry);
                        if (isSame(boxItem, target)) {
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

    private boolean isSame(ItemStack a, ItemStack b) {
        //? if < 1.17.0 {

        // return ItemStack.isSame(a, b);
        //? } else if <= 1.20.4 {

        /*return ItemStack.isSameItemSameTags(a, b);
         *///? } else {

        return ItemStack.isSameItemSameComponents(a, b);
        //? }
    }

    public void processAvailability(ServerPlayer player, RequestAvailability payload) {
        System.out.println(
                "Player " + player.getName().getString() + " requested availability for items: " + payload.items());
        List<RemoteItem> items = providerManager.findRemoteItems(player,
                payload.items().stream().map(s -> ItemUtil.getItem(McId.create(s).id())).collect(Collectors.toSet()));
        ServerNetworkUtil.sendPacket(player, new ItemAvailability(items));
    }
}

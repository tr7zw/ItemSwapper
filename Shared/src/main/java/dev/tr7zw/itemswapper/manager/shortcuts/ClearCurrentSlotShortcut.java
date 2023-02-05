package dev.tr7zw.itemswapper.manager.shortcuts;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

public class ClearCurrentSlotShortcut implements Shortcut {

    private final ItemEntry icon = new ItemEntry(Items.BARRIER, null, Component.literal("Clear Slot"));
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    private final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final SwitchItemOverlay overlay;

    public ClearCurrentSlotShortcut(SwitchItemOverlay overlay) {
        this.overlay = overlay;
    }

    @Override
    public ItemEntry getIcon() {
        return icon;
    }

    @Override
    public void invoke(ActionType action) {
        List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(Items.AIR, true, true);
        if (!slots.isEmpty()) {
            AvailableSlot slot = slots.get(0);
            OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
            if (event.canceled().get()) {
                // interaction canceled by some other mod
                return;
            }
            if (slot.inventory() == -1) {
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                        hudSlot, minecraft.player.getInventory().selected,
                        ClickType.SWAP, this.minecraft.player);
            } else {
                NetworkUtil.swapItem(slot.inventory(), slot.slot());
            }
            clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
        }
        if (action == ActionType.CLICK) {
            overlay.hideClearSlotShortcut = true;
            // reopen to re-init the UI
            overlay.openPage(overlay.getPageHistory().remove(overlay.getPageHistory().size() - 1));
        }
    }

    @Override
    public boolean acceptClose() {
        return true;
    }

    @Override
    public boolean acceptClick() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return !minecraft.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

}

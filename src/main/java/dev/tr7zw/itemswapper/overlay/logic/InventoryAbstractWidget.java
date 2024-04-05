package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import net.minecraft.world.inventory.ClickType;

public abstract class InventoryAbstractWidget extends ItemGridWidget {

    protected InventoryAbstractWidget(int x, int y) {
        super(x, y);
    }

    protected abstract List<AvailableSlot> getItem(int id);

    @Override
    protected void renderSlot(RenderContext graphics, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwrideAvailable) {
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty()) {
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player,
                    slots.get(0).item(), 1, SlotEffect.NONE, slots.get(0).amount().get()));
        }
    }

    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty()) {
            AvailableSlot slot = slots.get(0);
            if (!slot.item().isEmpty()) {
                overlay.openPage(ItemSwapperMod.instance.getItemGroupManager().getNextPage(null,
                        new ItemEntry(slot.item().getItem(), null), guiSlot.id() + 9));
            }
        }
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty()) {
            AvailableSlot slot = slots.get(0);
            if (slot.inventory() == -1) {
                OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
                if (event.canceled().get()) {
                    // interaction canceled by some other mod
                    return true;
                }
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot,
                        minecraft.player.getInventory().selected, ClickType.SWAP, this.minecraft.player);
                clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
                ItemSwapperSharedMod.instance.setLastItem(slot.item().getItem());
                ItemSwapperSharedMod.instance
                        .setLastPage(overlay.getLastPages().get(overlay.getLastPages().size() - 1));
                return false;
            }
        }
        return true;
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable) {
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(ItemUtil.getDisplayname(availableSlots.get(0).item()),
                    availableSlots.get(0).item(), false, yOffset, maxWidth);
        }
    }

}

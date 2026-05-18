package dev.tr7zw.itemswapper.overlay.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.manager.*;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.packets.*;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.*;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.world.item.*;

public class ListContentWidget extends ItemGridWidget {

    private final ItemManager itemManager = ItemSwapperSharedMod.instance.getItemManager();
    private final ItemList itemSelection;
    private final List<AvailableSlot> entries = new ArrayList<>();

    public ListContentWidget(ItemList items, int x, int y) {
        super(x, y);
        this.itemSelection = items;
        refreshList();
        ClientNetworkUtil.sendPacket(new RequestAvailability(ItemListing.of(itemSelection.getItems())));
        WidgetUtil.setupDynamicSlots(widgetArea, slots, entries.size());
    }

    @Override
    public void remoteUpdate() {
        refreshList();
        slots.clear();
        WidgetUtil.setupDynamicSlots(widgetArea, slots, entries.size());
    }

    private void refreshList() {
        entries.clear();
        if (itemSelection.isPaletteList()) {
            for (int i = 0; i < itemSelection.getItems().length; i++) {
                List<AvailableSlot> availableSlots = resolveItem(i);
                if (availableSlots.isEmpty() || !dev.tr7zw.transition.mc.ItemUtil.isSame(availableSlots.get(0).item(),
                        itemSelection.getItems()[i].getDefaultInstance())) {
                    entries.add(null);
                } else {
                    entries.add(availableSlots.get(0));
                }
            }
        }
        for (Item item : itemSelection.getItems()) {
            List<AvailableSlot> ids = providerManager.findSlotsMatchingItem(item, false, false);
            for (AvailableSlot id : ids) {
                if (!entries.contains(id)) {
                    entries.add(id);
                }
            }
        }
    }

    private List<AvailableSlot> resolveItem(int id) {
        return id > itemSelection.getItems().length - 1 ? Collections.emptyList()
                : providerManager.findSlotsMatchingItem(itemSelection.getItems()[id], false, false);
    }

    @Override
    protected void renderSlot(RenderContext graphics, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwrideAvailable) {
        AvailableSlot slot = guiSlot.id() < entries.size() ? entries.get(guiSlot.id()) : null;
        if (slot != null && !overwrideAvailable) {
            SlotEffect effect = itemSelection.isPaletteList() && guiSlot.id() >= itemSelection.getItems().length
                    ? SlotEffect.YELLOW
                    : SlotEffect.NONE;
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player, slot.item(), 1,
                    effect, slot.amount().get()));

        } else if (guiSlot.id() <= entries.size() - 1) {
            if (entries.get(guiSlot.id()) == null) {
                itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player,
                        itemSelection.getItems()[guiSlot.id()].getDefaultInstance(), 1,
                        !overwrideAvailable ? SlotEffect.RED : SlotEffect.NONE, 0));
            } else {
                itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player,
                        entries.get(guiSlot.id()).item(), 1,
                        guiSlot.id() < itemSelection.getItems().length ? SlotEffect.NONE : SlotEffect.YELLOW,
                        entries.get(guiSlot.id()).amount().get()));
            }
        }
    }

    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        // doesn't link anywhere
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        if (guiSlot.id() > entries.size() - 1) {
            return true;
        }
        AvailableSlot entry = entries.get(guiSlot.id());
        if (entry != null && !entry.item().isEmpty()) {
            return itemManager.grabItem(entry);
        } else if (guiSlot.id() < itemSelection.getItems().length && minecraft.player.isCreative()
                && configManager.getConfig().creativeCheatMode) {
            // stash away current item, if its not a default item to prevent item loss
            ItemStack itemInHand = GeneralUtil.getPlayer().getMainHandItem();
            if (!itemInHand.isEmpty() && !dev.tr7zw.transition.mc.ItemUtil.isSame(itemInHand,
                    itemInHand.getItem().getDefaultInstance())) {
                ItemSwapperSharedMod.instance.getItemManager().grabItem(Items.AIR, true);
            }
            minecraft.gameMode.handleCreativeModeItemAdd(
                    itemSelection.getItems()[guiSlot.id()].getDefaultInstance().copy(),
                    36 + InventoryUtil.getSelectedId(minecraft.player.getInventory()));
            minecraft.player.getInventory().setItem(InventoryUtil.getSelectedId(minecraft.player.getInventory()),
                    itemSelection.getItems()[guiSlot.id()].getDefaultInstance().copy());
            ItemSwapperSharedMod.instance.setLastItem(itemSelection.getItems()[guiSlot.id()]);
            ItemSwapperSharedMod.instance.setLastPage(overlay.getLastPages().get(overlay.getLastPages().size() - 1));
            return false;
        }
        return true;
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable,
            RenderContext graphics) {
        if (selected.id() > entries.size() - 1) {
            return;
        }
        AvailableSlot slot = entries.get(selected.id());
        if (slot == null) {
            if (itemSelection.isPaletteList()) {
                RenderHelper.renderSelectedItemName(
                        ItemSwapperSharedMod.instance.getItemManager()
                                .getDisplayname(itemSelection.getItems()[selected.id()].getDefaultInstance()),
                        itemSelection.getItems()[selected.id()].getDefaultInstance(), false, yOffset, maxWidth,
                        graphics);
            }
            return;
        }
        RenderHelper.renderSelectedItemName(ItemSwapperSharedMod.instance.getItemManager().getDisplayname(slot.item()),
                slot.item(), false, yOffset, maxWidth, graphics);

    }

}

package dev.tr7zw.itemswapper.overlay.logic;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class PaletteWidget extends ItemGridWidget {

    private final ItemGroup itemGroup;

    public PaletteWidget(ItemGroup itemGroup, int x, int y) {
        super(x, y);
        this.itemGroup = itemGroup;
        WidgetUtil.setupDynamicSlots(widgetArea, slots, itemGroup.getItems().length);
    }

    private List<AvailableSlot> getItem(int id) {
        return id > itemGroup.getItems().length - 1 ? Collections.emptyList()
                : providerManager.findSlotsMatchingItem(itemGroup.getItems()[id].getItem(), false, false);
    }

    @Override
    protected void renderSlot(PoseStack poseStack, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwrideAvailable) {
        ItemEntry entry = itemGroup.getItem(guiSlot.id());
        if(entry != null && entry.isActAsLink()) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player, entry.getItem().getDefaultInstance(), 1,
                            SlotEffect.NONE, 1));
            return;
        }
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty() && !overwrideAvailable) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player, slots.get(0).item(), 1,
                            SlotEffect.NONE, slots.get(0).amount().get()));

        } else if (guiSlot.id() <= itemGroup.getItems().length - 1) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player,
                            itemGroup.getItems()[guiSlot.id()].getItem().getDefaultInstance(), 1,
                            !overwrideAvailable ? SlotEffect.RED : SlotEffect.NONE, 1));
        }
    }

    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        ItemEntry entry = itemGroup.getItem(slot.id());
        if (entry != null && entry.getItem() != Items.AIR) {
            // try to open the new page
            if (overlay.openPage(ItemSwapperMod.instance.getItemGroupManager().getNextPage(itemGroup, entry, -1))) {
                overlay.selectIcon("item|" + Item.getId(entry.getItem()), xOffset, yOffset);
            }
        }
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        ItemEntry entry = itemGroup.getItem(guiSlot.id());
        if(entry != null && entry.isActAsLink()) {
            onSecondaryClick(overlay, guiSlot, xOffset, yOffset);
            return true;
        }
        if (entry != null && entry.getItem() != Items.AIR) {
            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
                minecraft.gameMode.handleCreativeModeItemAdd(entry.getItem().getDefaultInstance().copy(),
                        36 + minecraft.player.getInventory().selected);
                ItemSwapperSharedMod.instance.setLastItem(entry.getItem());
                ItemSwapperSharedMod.instance.setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
                return false;
            }
            boolean changed = ItemUtil.grabItem(entry.getItem(), false);
            if(changed) {
                ItemSwapperSharedMod.instance.setLastItem(entry.getItem());
                ItemSwapperSharedMod.instance.setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
                return false;
            }
        }
        return true;
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable) {
        ItemEntry slot = itemGroup.getItem(selected.id());
        if (slot == null) {
            return;
        }
        if(slot.isActAsLink()) {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(itemGroup.getItem(selected.id())),
                    slot.getItem().getDefaultInstance(), false, yOffset, maxWidth);
            return;
        }
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(itemGroup.getItem(selected.id())),
                    availableSlots.get(0).item(), false, yOffset, maxWidth);
        } else {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(itemGroup.getItem(selected.id())),
                    slot.getItem().getDefaultInstance(), !overwrideAvailable, yOffset, maxWidth);
        }

    }

    @Override
    public String getSelector(GuiSlot slot) {
        ItemEntry entry = itemGroup.getItem(slot.id());
        if(entry != null) {
            return "item|" + Item.getId(entry.getItem());
        }
        return null;
    }

}

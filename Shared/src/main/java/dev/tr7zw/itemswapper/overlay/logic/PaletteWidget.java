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
import dev.tr7zw.itemswapper.util.WidgetUtil;
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
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty() && !overwrideAvailable) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player, slots.get(0).item(), 1,
                            false, slots.get(0).amount().get()));

        } else if (guiSlot.id() <= itemGroup.getItems().length - 1) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player,
                            itemGroup.getItems()[guiSlot.id()].getItem().getDefaultInstance(), 1,
                            !overwrideAvailable, 1));
        }
    }

    @Override
    public void onClick(SwitchItemOverlay overlay, GuiSlot slot) {
        ItemEntry entry = itemGroup.getItem(slot.id());
        if (entry != null && entry.getItem() != Items.AIR) {
            overlay.openPage(ItemSwapperMod.instance.getItemGroupManager().getNextPage(itemGroup, entry, -1));

        }
    }

    @Override
    public void onClose(SwitchItemOverlay overlay, GuiSlot guiSlot) {
        ItemEntry entry = itemGroup.getItem(guiSlot.id());
        if (entry != null && entry.getItem() != Items.AIR) {
            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
                minecraft.gameMode.handleCreativeModeItemAdd(entry.getItem().getDefaultInstance().copy(),
                        36 + minecraft.player.getInventory().selected);
                ItemSwapperSharedMod.instance.setLastItem(entry.getItem());
                ItemSwapperSharedMod.instance.setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
                return;
            }
            boolean changed = ItemUtil.grabItem(entry.getItem(), false);
            if(changed) {
                ItemSwapperSharedMod.instance.setLastItem(entry.getItem());
                ItemSwapperSharedMod.instance.setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
            }
        }
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, boolean overwrideAvailable) {
        ItemEntry slot = itemGroup.getItem(selected.id());
        if (slot == null) {
            return;
        }
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(itemGroup.getItem(selected.id())),
                    availableSlots.get(0).item(), false, yOffset);
        } else {
            RenderHelper.renderSelectedItemName(RenderHelper.getName(itemGroup.getItem(selected.id())),
                    slot.getItem().getDefaultInstance(), !overwrideAvailable, yOffset);
        }

    }

}

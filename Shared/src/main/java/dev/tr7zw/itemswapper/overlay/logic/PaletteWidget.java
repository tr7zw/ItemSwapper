package dev.tr7zw.itemswapper.overlay.logic;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.world.inventory.ClickType;
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
    protected void renderSlot(PoseStack poseStack, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot, boolean overwrideAvailable) {
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
            ItemGroup sel = ItemSwapperMod.instance.getItemGroupManager().getNextPage(itemGroup, entry);
            if (sel != null) {
                overlay.openItemGroup(sel);
            }
        }
    }

    @Override
    public void onClose(SwitchItemOverlay overlay, GuiSlot guiSlot) {
        ItemEntry entry = itemGroup.getItem(guiSlot.id());
        if (entry != null && entry.getItem() != Items.AIR) {
          if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
              minecraft.gameMode.handleCreativeModeItemAdd(entry.getItem().getDefaultInstance().copy(),
                      36 + minecraft.player.getInventory().selected);
              return;
          }
          List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(entry.getItem(), true, false);
          if (!slots.isEmpty()) {
              AvailableSlot slot = slots.get(0);
              OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
              if(event.canceled().get()) {
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

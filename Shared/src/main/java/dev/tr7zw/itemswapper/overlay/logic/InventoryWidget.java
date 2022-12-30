package dev.tr7zw.itemswapper.overlay.logic;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class InventoryWidget extends ItemGridWidget {
    
    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inventory.png");

    public InventoryWidget(int x, int y) {
        super(x, y);
        setupSlots(9, 3, false, BACKGROUND_LOCATION);
        widgetArea.setBackgroundTextureSizeX(168);
        widgetArea.setBackgroundTextureSizeY(60);
    }

    private List<AvailableSlot> getItem(int id) {
      NonNullList<ItemStack> items = minecraft.player.getInventory().items;
      if (id != -1 && !items.get(id + 9).isEmpty()) {
          return Collections.singletonList(new AvailableSlot(-1, id + 9, items.get(id + 9)));
      }
      return Collections.emptyList();
    }

    protected void renderSelection(GuiComponent parent, PoseStack poseStack, int listId, int x, int y,
            List<Runnable> itemRenderList,
            List<Runnable> lateRenderList,
            boolean overwrideAvailable) {
        if (widgetArea.getBackgroundTexture() == null) {
            parent.blit(poseStack, x, y, 24, 22, 29, 24);
        }
        GuiSlot guiSlot = slots.get(listId);
        if (guiSlot.selected().get()) {
            itemRenderList = lateRenderList;
            lateRenderList.add(() -> {
                float blit = parent.getBlitOffset();
                parent.setBlitOffset((int) this.itemRenderer.blitOffset);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, SELECTION_LOCATION);
                GuiComponent.blit(poseStack, x - 1, y, parent.getBlitOffset(), 0, 0, 24, 24, 24, 24);
                parent.setBlitOffset((int) blit);
            });
        }
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty()) {
            itemRenderList.add(
                    () -> RenderHelper.renderSlot(poseStack, x + 3, y + 4, minecraft.player, slots.get(0).item(), 1,
                            false, slots.get(0).amount().get()));
        }
    }

    @Override
    public void onClick(SwitchItemOverlay overlay, GuiSlot guiSlot) {
      List<AvailableSlot> slots = getItem(guiSlot.id());
      if (!slots.isEmpty()) {
          AvailableSlot slot = slots.get(0);
          if (!slot.item().isEmpty()) {
              ItemGroup sel = ItemSwapperMod.instance.getItemGroupManager().getItemPage(slot.item().getItem());
              if (sel != null) {
                  overlay.openItemGroup(sel);
              }
          }
      }
    }

    @Override
    public void onClose(SwitchItemOverlay overlay, GuiSlot guiSlot) {
      List<AvailableSlot> slots = getItem(guiSlot.id());
      if (!slots.isEmpty()) {
          AvailableSlot slot = slots.get(0);
          if (slot.inventory() == -1) {
              OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
              if(event.canceled().get()) {
                  // interaction canceled by some other mod
                  return;
              }
              int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
              this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                      hudSlot, minecraft.player.getInventory().selected,
                      ClickType.SWAP, this.minecraft.player);
              clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
          }
      }
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, boolean overwrideAvailable) {
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(getDisplayname(availableSlots.get(0).item()),
                    availableSlots.get(0).item(), false, yOffset);
        }
    }
    
    private Component getDisplayname(ItemStack item) {
        if (item.hasCustomHoverName()) {
            return item.getHoverName();
        }
        NameProvider provider = providerManager.getNameProvider(item.getItem());
        if(provider != null) {
            return provider.getDisplayName(item);
        }
        return item.getHoverName();
    }

}

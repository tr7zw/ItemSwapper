package dev.tr7zw.itemswapper.overlay.logic;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;

import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.manager.*;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.InventoryUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.*;
import net.minecraft.world.item.ItemStack;

public class ContainerWidget extends ItemGridWidget {

    private static final Identifier BACKGROUND_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/inventory.png");

    private static final ClientProviderManager providerManager = ItemSwapperSharedMod.instance
            .getClientProviderManager();
    private static final ItemManager itemManager = ItemSwapperSharedMod.instance.getItemManager();
    private int slotId;

    public ContainerWidget(int x, int y, int slotId) {
        super(x, y);
        this.slotId = slotId;
        WidgetUtil.setupSlots(widgetArea, slots, 9, 3, false, BACKGROUND_LOCATION);
        widgetArea.setBackgroundTextureSizeX(168);
        widgetArea.setBackgroundTextureSizeY(60);
    }

    private NonNullList<AvailableSlot> getItems() {
        ItemStack item = InventoryUtil.getNonEquipmentItems(minecraft.player.getInventory()).get(slotId);
        ContainerProvider provider = providerManager.getContainerProvider(item.getItem());
        if (provider == null) {
            return NonNullList.create();
        }
        return provider.getItemStacks(item, slotId);
    }

    private List<AvailableSlot> getItem(int id) {
        NonNullList<AvailableSlot> items = getItems();
        if (items.size() <= id) {
            return Collections.emptyList();
        }
        if (id != -1 && !items.get(id).item().isEmpty()) {
            return Collections.singletonList(items.get(id));
        }
        return Collections.emptyList();
    }

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
                        new ItemEntry(slot.item().getItem(), null), -1));
            }
        }
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty()) {
            AvailableSlot slot = slots.get(0);
            if (!itemManager.grabItem(slot)) {
                // interaction canceled by some other mod
                return true;
            }
            ItemSwapperSharedMod.instance.setLastItem(slot.item().getItem());
            ItemSwapperSharedMod.instance.setLastPage(overlay.getLastPages().get(overlay.getLastPages().size() - 1));
            return false;
        }
        return true;
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable,
            RenderContext graphics) {
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(
                    ItemSwapperSharedMod.instance.getItemManager().getDisplayname(availableSlots.get(0).item()),
                    availableSlots.get(0).item(), false, yOffset, maxWidth, graphics);
        }
    }

}

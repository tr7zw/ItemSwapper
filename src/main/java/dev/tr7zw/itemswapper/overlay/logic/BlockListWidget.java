package dev.tr7zw.itemswapper.overlay.logic;

import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class BlockListWidget extends ItemGridWidget {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inventory.png");
    private static final ClientProviderManager providerManager = ItemSwapperSharedMod.instance
            .getClientProviderManager();
    private List<Block> blocks;

    public BlockListWidget(int x, int y, List<Block> blocks) {
        super(x, y);
        this.blocks = blocks;
        WidgetUtil.setupSlots(widgetArea, slots, 9, 3, false, BACKGROUND_LOCATION);
        widgetArea.setBackgroundTextureSizeX(168);
        widgetArea.setBackgroundTextureSizeY(60);
    }

    private NonNullList<AvailableSlot> getItems() {
        NonNullList<AvailableSlot> list = NonNullList.create();
        for(Block b : blocks) {
            list.addAll(providerManager.findSlotsMatchingItem(b.asItem(), true, false));
        }
        return list;
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
    protected void renderSlot(GuiGraphics graphics, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwrideAvailable) {
        List<AvailableSlot> slots = getItem(guiSlot.id());
        if (!slots.isEmpty() && !overwrideAvailable) {
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player,
                    slots.get(0).item(), 1, SlotEffect.NONE, slots.get(0).amount().get()));

        } else if (guiSlot.id() <= blocks.size()) {
            itemRenderList.add(() -> RenderHelper.renderSlot(graphics, x + 3, y + 4, minecraft.player,
                    blocks.get(guiSlot.id()).asItem().getDefaultInstance(), 1,
                    !overwrideAvailable ? SlotEffect.RED : SlotEffect.NONE, 1));
        }
    }

    @Override
    public boolean onPrimaryClick(SwitchItemOverlay overlay, GuiSlot guiSlot, int xOffset, int yOffset) {
        Item item = blocks.get(guiSlot.id()).asItem();
        if (item != null && item != Items.AIR) {
            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
                minecraft.gameMode.handleCreativeModeItemAdd(item.getDefaultInstance().copy(),
                        36 + minecraft.player.getInventory().selected);
                ItemSwapperSharedMod.instance.setLastItem(item);
                ItemSwapperSharedMod.instance
                        .setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
                return false;
            }
            boolean changed = ItemUtil.grabItem(item, false);
            if (changed) {
                ItemSwapperSharedMod.instance.setLastItem(item);
                ItemSwapperSharedMod.instance
                        .setLastPage(overlay.getPageHistory().get(overlay.getPageHistory().size() - 1));
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        // nothing
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable) {
        List<AvailableSlot> availableSlots = getItem(selected.id());
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(availableSlots.get(0).item().getDisplayName(),
                    availableSlots.get(0).item(), false, yOffset, maxWidth);
        } else {
            RenderHelper.renderSelectedItemName(ItemUtil.getDisplayname(blocks.get(selected.id()).asItem().getDefaultInstance()),
                    blocks.get(selected.id()).asItem().getDefaultInstance(), !overwrideAvailable, yOffset, maxWidth);
        }

    }
}

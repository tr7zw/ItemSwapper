package dev.tr7zw.itemswapper.overlay.logic;

import java.util.List;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.TexturePage;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.RenderHelper.SlotEffect;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import net.minecraft.client.gui.GuiGraphics;
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

    @Override
    protected void renderSlot(RenderContext graphics, int x, int y, List<Runnable> itemRenderList, GuiSlot guiSlot,
            boolean overwrideAvailable) {
        if (guiSlot.id() >= blocks.size()) {
            return;
        }
        Item item = blocks.get(guiSlot.id()).asItem();
        List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(item, true, false);
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
                        .setLastPage(overlay.getLastPages().get(overlay.getLastPages().size() - 1));
                return false;
            }
            boolean changed = ItemUtil.grabItem(item, false);
            if (changed) {
                ItemSwapperSharedMod.instance.setLastItem(item);
                ItemSwapperSharedMod.instance
                        .setLastPage(overlay.getLastPages().get(overlay.getLastPages().size() - 1));
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSecondaryClick(SwitchItemOverlay overlay, GuiSlot slot, int xOffset, int yOffset) {
        UnpackedColor[] color = ItemSwapperSharedMod.instance.getBlockTextureManager().getColor(blocks.get(slot.id()));
        ItemSwapperSharedMod.instance.openPage(new TexturePage(color, color[0]));
    }

    @Override
    public void renderSelectedSlotName(GuiSlot selected, int yOffset, int maxWidth, boolean overwrideAvailable) {
        if (selected.id() >= blocks.size()) {
            return;
        }
        Item item = blocks.get(selected.id()).asItem();
        List<AvailableSlot> availableSlots = providerManager.findSlotsMatchingItem(item, true, false);
        if (!availableSlots.isEmpty() && !overwrideAvailable) {
            RenderHelper.renderSelectedItemName(availableSlots.get(0).item().getDisplayName(),
                    availableSlots.get(0).item(), false, yOffset, maxWidth);
        } else {
            RenderHelper.renderSelectedItemName(
                    ItemUtil.getDisplayname(blocks.get(selected.id()).asItem().getDefaultInstance()),
                    blocks.get(selected.id()).asItem().getDefaultInstance(), !overwrideAvailable, yOffset, maxWidth);
        }

    }
}

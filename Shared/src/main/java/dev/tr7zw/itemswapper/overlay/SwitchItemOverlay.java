package dev.tr7zw.itemswapper.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.logic.GuiSelectionHandler;
import dev.tr7zw.itemswapper.overlay.logic.GuiWidget;
import dev.tr7zw.itemswapper.overlay.logic.PaletteWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class SwitchItemOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    
    public final Minecraft minecraft = Minecraft.getInstance();
    public final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    public final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final GuiSelectionHandler selectionHandler = new GuiSelectionHandler();
    public int globalXOffset = 0;
    public int globalYOffset = 0;
    public boolean forceAvailable = false;
    public boolean hideCursor = false;
    
    private final ConfigManager configManager = ConfigManager.getInstance();
    private ItemGroup backingItemGroup;


    public SwitchItemOverlay(ItemGroup itemGroup) {
        this.backingItemGroup = itemGroup;

        setupSlots();
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            forceAvailable = true;
        }
    }

    public abstract void setupSlots();

    @Override
    public void render(PoseStack poseStack, int no1, int no2, float f) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 + globalXOffset;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;
        for(GuiWidget widget : selectionHandler.getWidgets()) {
            widget.render(this, poseStack, originX, originY, forceAvailable);
        }
        if(selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().renderSelectedSlotName(selectionHandler.getSelectedSlot(), selectionHandler.getWidgets().get(0).titleYOffset(), forceAvailable);
        }

        if (configManager.getConfig().showCursor && !hideCursor) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            blit(poseStack, originX + (int) selectionHandler.getCursorX() - 8, originY + (int) selectionHandler.getCursorY() - 8, 240, 0, 15, 15);
            poseStack.popPose();
        }
    }

    public boolean forceItemsAvailable() {
        return forceAvailable;
    }
    
    public void handleInput(double x, double y) {
        selectionHandler.updateSelection(x, y);
    }

    public void handleSwitchSelection() {
        // Don't allow switching if there is no second set
//        if (getSelection() != -1 && getSelection() < itemGroup.getItems().length && itemGroup.getItems()[getSelection()].getItem() != Items.AIR) {
//            ItemGroup sel = ItemSwapperMod.instance.getItemGroupManager().getNextPage(itemGroup, itemGroup.getItems()[getSelection()]);
//            if(sel != null) {
//                this.itemGroup = sel;
//            }
//        }
        setupSlots();
    }

    public void onClose() {
//        if(getSelection() == -1) {
//            return;
//        }
//        GuiSlot guiSlot = getGuiSlots().get(getSelection());
//        if (guiSlot.type() == SlotType.SHORTCUT) {
//            Shortcut shortCut = itemGroup.getRightSideShortcuts().get(guiSlot.id());
//            if (shortCut.acceptLeftclick()) {
//                shortCut.invoke();
//            }
//            return;
//        }
//        if (getSelection() < itemGroup.getItems().length && itemGroup.getItems()[getSelection()].getItem() != Items.AIR) {
//            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
////                minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, 36 + minecraft.player.getInventory().selected);
//                minecraft.gameMode.handleCreativeModeItemAdd(itemGroup.getItems()[getSelection()].getItem().getDefaultInstance().copy(),
//                        36 + minecraft.player.getInventory().selected);
//                return;
//            }
//            List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(itemGroup.getItems()[getSelection()].getItem(), true, false);
//            if (!slots.isEmpty()) {
//                AvailableSlot slot = slots.get(0);
//                OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
//                if(event.canceled().get()) {
//                    // interaction canceled by some other mod
//                    return;
//                }
//                if (slot.inventory() == -1) {
//                    int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
//                    this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
//                            hudSlot, minecraft.player.getInventory().selected,
//                            ClickType.SWAP, this.minecraft.player);
//                } else {
//                    NetworkUtil.swapItem(slot.inventory(), slot.slot());
//                }
//                clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
//            }
//        }
    }

    public void setupSlots(int width, int height, boolean skipCorners, ResourceLocation texture) {
        selectionHandler.reset();
        selectionHandler.addWidget(new PaletteWidget(backingItemGroup, height, height, width, height, skipCorners, texture));
    }

    public ItemGroup getItemGroup() {
        return backingItemGroup;
    }

}

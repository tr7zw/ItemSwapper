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
import dev.tr7zw.itemswapper.overlay.logic.InventoryWidget;
import dev.tr7zw.itemswapper.overlay.logic.PaletteWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class SwitchItemOverlay extends XTOverlay {

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


    private SwitchItemOverlay() {
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            forceAvailable = true;
        }
    }

    public static SwitchItemOverlay createPaletteOverlay(ItemGroup itemGroup) {
        SwitchItemOverlay overlay = new SwitchItemOverlay();
        overlay.openItemGroup(itemGroup);
        return overlay;
    }
    
    public static SwitchItemOverlay createInventoryOverlay() {
        SwitchItemOverlay overlay = new SwitchItemOverlay();
        overlay.openInventory();
        return overlay;
    }
    
    public void openItemGroup(ItemGroup itemGroup) {
        selectionHandler.reset();
        selectionHandler.addWidget(new PaletteWidget(itemGroup, 0, 0));
    }
    
    public void openInventory() {
        selectionHandler.reset();
        selectionHandler.addWidget(new InventoryWidget(0, 0));
    }

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
        if(selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onClick(this, selectionHandler.getSelectedSlot());
        }
    }

    public void onClose() {
        if(selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onClose(this, selectionHandler.getSelectedSlot());
        }
//        GuiSlot guiSlot = getGuiSlots().get(getSelection());
//        if (guiSlot.type() == SlotType.SHORTCUT) {
//            Shortcut shortCut = itemGroup.getRightSideShortcuts().get(guiSlot.id());
//            if (shortCut.acceptLeftclick()) {
//                shortCut.invoke();
//            }
//            return;
//        }

    }

}

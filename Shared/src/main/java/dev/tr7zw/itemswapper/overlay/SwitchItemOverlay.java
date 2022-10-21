package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.ItemUtil.Slot;
import dev.tr7zw.itemswapper.util.NetworkLogic;
import dev.tr7zw.itemswapper.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SwitchItemOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final double limit = 100;
    private static final double deadZone = limit / 3 / 2;
    private static final int slotSize = 22;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private Item[] itemSelection;
    private Item[] secondaryItemSelection;
    private GuiSlot[] guiSlots;

    private double selectX = 0;
    private double selectY = 0;
    private Selection selection = null;

    public SwitchItemOverlay(Item[] selection, Item[] selectionSecondary) {
        this.itemSelection = selection;
        this.secondaryItemSelection = selectionSecondary;
        setup8Slots();
    }
    
    @Override
    public void render(PoseStack poseStack, int no1, int no2, float f) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        List<Runnable> itemRenderList = new ArrayList<>();
        for(int i = 0; i < guiSlots.length; i++) {
            renderSelection(poseStack, i, originX + guiSlots[i].x, originY + guiSlots[i].y, itemRenderList);
        }
        itemRenderList.forEach(Runnable::run);
    }
    
    private void renderSelection(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList) {
        blit(poseStack, x, y, 24, 22, 29, 24);
        List<Slot> slots = ItemUtil.findSlotsMatchingItem(itemSelection[id]);
        if(!slots.isEmpty()) {
            itemRenderList.add(() -> renderSlot(x+3, y+4, minecraft.player, slots.get(0).item(), 1, false));
        } else {
            itemRenderList.add(() -> renderSlot(x+3, y+4, minecraft.player, itemSelection[id].getDefaultInstance(), 1, true));
        }
        if(selection != null && selection.ordinal() == id) {
            blit(poseStack, x-1, y, 0, 22, 24, 24);
        }
    }

    public void handleInput(double x, double y) {
        selectX += x;
        selectY += y;
        selectX = Mth.clamp(selectX, -limit, limit);
        selectY = Mth.clamp(selectY, -limit, limit);
        updateSelection();
    }
    
    public void handleSwitchSelection() {
        // Don't allow switching if there is no second set
        if(secondaryItemSelection == null) {
            return;
        }
        Item[] tmp = itemSelection;
        itemSelection = secondaryItemSelection;
        secondaryItemSelection = tmp;
    }

    private void updateSelection() {
        if (selectY < -deadZone) { // up(mc has the Y flipped)
            if (selectX > deadZone) {
                selection = Selection.TOP_RIGHT;
            } else if (selectX < -deadZone) {
                selection = Selection.TOP_LEFT;
            } else {
                selection = Selection.TOP;
            }
        } else if (selectY > deadZone) { // down(mc has the Y flipped)
            if (selectX > deadZone) {
                selection = Selection.BOTTOM_RIGHT;
            } else if (selectX < -deadZone) {
                selection = Selection.BOTTOM_LEFT;
            } else {
                selection = Selection.BOTTOM;
            }
        } else if (Math.abs(selectX) > deadZone) { // just left/right
            if (selectX > deadZone) {
                selection = Selection.RIGHT;
            } else if (selectX < -deadZone) {
                selection = Selection.LEFT;
            }
        } else {
            selection = null;
        }
    }

    public void onClose() {
        if(selection != null && itemSelection[selection.ordinal()] != Items.AIR) {
            List<Slot> slots = ItemUtil.findSlotsMatchingItem(itemSelection[selection.ordinal()]);
            if(!slots.isEmpty()) {
                Slot slot = slots.get(0);
                if(slot.inventory() == -1) {
                    int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                    this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot, minecraft.player.getInventory().selected,
                            ClickType.SWAP, this.minecraft.player);
                }else {
                    NetworkLogic.swapItem(slot.inventory(), slot.slot());
                }
            }
        }
    }

    private void renderSlot(int x, int y, Player arg, ItemStack arg2, int k, boolean grayOut) {
        if (!arg2.isEmpty()) {
            if(grayOut) {
                RenderHelper.renderGrayedOutItem(arg, arg2, x, y, k);
                return;
            }
            this.itemRenderer.renderAndDecorateItem(arg, arg2, x, y, k);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, arg2, x, y);
        }
    }

    private void setup8Slots() {
        guiSlots = new GuiSlot[8];
        int originX = -slotSize - (slotSize / 2);
        int originY = -slotSize - (slotSize / 2) - 1;
        for (int i = 0; i < 3; i++) {
            guiSlots[i] = new GuiSlot(originX + i * slotSize, originY);
            guiSlots[i + 3] = new GuiSlot(originX + i * slotSize, originY + slotSize * 2);
            if (i == 0 || i == 2) {
                guiSlots[i == 0 ? 6 : 7] = new GuiSlot(originX + i * slotSize, originY + slotSize);
            }
        }
    }
    
    private record GuiSlot(int x, int y) {}
    
    public enum Selection {
        TOP_LEFT, TOP, TOP_RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT, LEFT, RIGHT, 
    }

}

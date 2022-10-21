package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
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
    private static final double limit = 33;
    private static final double deadZone = 11;
    private static final int slotSize = 22;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private Item[] itemSelection;
    private Item[] secondaryItemSelection;
    private GuiSlot[] guiSlots;

    private double selectX = 0;
    private double selectY = 0;
    private int selection = -1;

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
        for (int i = 0; i < guiSlots.length; i++) {
            renderSelection(poseStack, i, originX + guiSlots[i].x, originY + guiSlots[i].y, itemRenderList);
        }
        itemRenderList.forEach(Runnable::run);
        if(ItemSwapperSharedMod.instance.config.showCursor) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            blit(poseStack, originX + (int)selectX - 8, originY + (int)selectY - 8, 240, 0, 15, 15);
            poseStack.popPose();
        }
    }

    private void renderSelection(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList) {
        blit(poseStack, x, y, 24, 22, 29, 24);
        List<Slot> slots = ItemUtil.findSlotsMatchingItem(itemSelection[id]);
        if (!slots.isEmpty()) {
            itemRenderList.add(() -> renderSlot(x + 3, y + 4, minecraft.player, slots.get(0).item(), 1, false));
        } else {
            itemRenderList.add(
                    () -> renderSlot(x + 3, y + 4, minecraft.player, itemSelection[id].getDefaultInstance(), 1, true));
        }
        if (selection == id) {
            blit(poseStack, x - 1, y, 0, 22, 24, 24);
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
        if (secondaryItemSelection == null) {
            return;
        }
        Item[] tmp = itemSelection;
        itemSelection = secondaryItemSelection;
        secondaryItemSelection = tmp;
    }

    private void updateSelection() {
        selection = -1;
        double centerDist = Math.sqrt(selectX * selectX + selectY * selectY);
        if (centerDist < deadZone) {
            return;
        }
        double best = Double.MAX_VALUE;
        int halfSlot = slotSize / 2;
        for (int i = 0; i < guiSlots.length; i++) {
            double mouseDist = Math.sqrt((selectX - guiSlots[i].x - halfSlot) * (selectX - guiSlots[i].x - halfSlot)
                    + (selectY - guiSlots[i].y - halfSlot) * (selectY - guiSlots[i].y - halfSlot));
            if(mouseDist < best) {
                best = mouseDist;
                selection = i;
            }
        }
    }

    public void onClose() {
        if (selection != -1 && itemSelection[selection] != Items.AIR) {
            List<Slot> slots = ItemUtil.findSlotsMatchingItem(itemSelection[selection]);
            if (!slots.isEmpty()) {
                Slot slot = slots.get(0);
                if (slot.inventory() == -1) {
                    int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                    this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                            hudSlot, minecraft.player.getInventory().selected,
                            ClickType.SWAP, this.minecraft.player);
                } else {
                    NetworkLogic.swapItem(slot.inventory(), slot.slot());
                }
            }
        }
    }

    private void renderSlot(int x, int y, Player arg, ItemStack arg2, int k, boolean grayOut) {
        if (!arg2.isEmpty()) {
            if (grayOut) {
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

    private record GuiSlot(int x, int y) {
    }

}

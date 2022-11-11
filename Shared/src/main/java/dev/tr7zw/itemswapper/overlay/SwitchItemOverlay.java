package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ConfigManager;
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
    private static final ResourceLocation BACKGROUND_8_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_8.png");
    private static final ResourceLocation BACKGROUND_12_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_12.png");
    private static final ResourceLocation BACKGROUND_16_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_16.png");
    private static final ResourceLocation BACKGROUND_20_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_20.png");
    private final ConfigManager configManager = ConfigManager.getInstance();
    private double limitX = 33;
    private double limitY = 33;
    private double deadZone = 11;
    private static final int slotSize = 22;
    private static final int tinySlotSize = 18;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private Item[] itemSelection;
    private Item[] secondaryItemSelection;
    private GuiSlot[] guiSlots;
    private int backgroundSize = 0;
    private ResourceLocation backgroundTexture = null;
    
    private double selectX = 0;
    private double selectY = 0;
    private int selection = -1;

    public SwitchItemOverlay(Item[] selection, Item[] selectionSecondary) {
        this.itemSelection = selection;
        this.secondaryItemSelection = selectionSecondary;
        setupSlots();
    }

    private void setupSlots() {
        if (itemSelection.length <= 8) {
            setup8Slots();
            return;
        }
        if (itemSelection.length <= 12) {
            setup12Slots();
            return;
        }
        if (itemSelection.length <= 16) {
            setup16Slots();
            return;
        }
        setup20Slots();
    }

    @Override
    public void render(PoseStack poseStack, int no1, int no2, float f) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (backgroundSize > 0 && backgroundTexture != null) {
            RenderSystem.setShaderTexture(0, backgroundTexture);
            blit(poseStack, originX - (backgroundSize / 2), originY - (backgroundSize / 2), 0, 0, backgroundSize, backgroundSize, 128, 128);
        }
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        List<Runnable> itemRenderList = new ArrayList<>();
        List<Runnable> lateRenderList = new ArrayList<>();
        for (int i = 0; i < guiSlots.length; i++) {
            renderSelection(poseStack, i, originX + guiSlots[i].x, originY + guiSlots[i].y, itemRenderList,
                    lateRenderList);
        }
        itemRenderList.forEach(Runnable::run);
        float blit = this.itemRenderer.blitOffset;
        this.itemRenderer.blitOffset += 300;
        lateRenderList.forEach(Runnable::run);
        this.itemRenderer.blitOffset = blit;
        if (configManager.getConfig().showCursor) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            blit(poseStack, originX + (int) selectX - 8, originY + (int) selectY - 8, 240, 0, 15, 15);
            poseStack.popPose();
        }
    }

    private void renderSelection(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList,
            List<Runnable> lateRenderList) {
        if (backgroundTexture == null) {
            blit(poseStack, x, y, 24, 22, 29, 24);
        }
        List<Slot> slots = id > itemSelection.length - 1 ? Collections.emptyList()
                : ItemUtil.findSlotsMatchingItem(itemSelection[id], true);
        if (selection == id) {
            itemRenderList = lateRenderList;
            lateRenderList.add(() -> {
                float blit = getBlitOffset();
                setBlitOffset((int) this.itemRenderer.blitOffset);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
                blit(poseStack, x - 1, y, 0, 22, 24, 24);
                setBlitOffset((int) blit);
            });
        }

        if (!slots.isEmpty()) {
            itemRenderList.add(() -> renderSlot(x + 3, y + 4, minecraft.player, slots.get(0).item(), 1, false));
        } else if (id <= itemSelection.length - 1) {
            itemRenderList.add(
                    () -> renderSlot(x + 3, y + 4, minecraft.player, itemSelection[id].getDefaultInstance(), 1, true));
        }
    }

    public void handleInput(double x, double y) {
        selectX += x;
        selectY += y;
        selectX = Mth.clamp(selectX, -limitX, limitX);
        selectY = Mth.clamp(selectY, -limitY, limitY);
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
        setupSlots();
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
            if (mouseDist < best) {
                best = mouseDist;
                selection = i;
            }
        }
    }

    public void onClose() {
        if (selection != -1 && itemSelection[selection] != Items.AIR) {
            List<Slot> slots = ItemUtil.findSlotsMatchingItem(itemSelection[selection], true);
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
        backgroundTexture = BACKGROUND_8_LOCATION;
        backgroundSize = 60;
        limitX = 33;
        limitY = 33;
        deadZone = 11;
        guiSlots = new GuiSlot[8];
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            guiSlots[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            guiSlots[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                guiSlots[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
    }

    private void setup12Slots() {
        backgroundTexture = BACKGROUND_12_LOCATION;
        backgroundSize = 96;
        limitX = 44;
        limitY = 33;
        deadZone = 11;
        guiSlots = new GuiSlot[12];
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            guiSlots[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            guiSlots[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                guiSlots[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 2; i++) {
            guiSlots[i * 2 + 8] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
            guiSlots[i * 2 + 9] = new GuiSlot(originX + 3 * tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
        }
    }

    
    private void setup16Slots() {
        backgroundTexture = BACKGROUND_16_LOCATION;
        backgroundSize = 96;
        limitX = 44;
        limitY = 44;
        deadZone = 11;
        guiSlots = new GuiSlot[16];
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            guiSlots[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            guiSlots[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                guiSlots[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 2; i++) {
            guiSlots[i * 2 + 8] = new GuiSlot(originX + i * tinySlotSize + tinySlotSize / 2, originY - tinySlotSize);
            guiSlots[i * 2 + 9] = new GuiSlot(originX + i * tinySlotSize + tinySlotSize / 2, originY + tinySlotSize * 3);
        }
        for (int i = 0; i < 2; i++) {
            guiSlots[i * 2 + 12] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
            guiSlots[i * 2 + 13] = new GuiSlot(originX + 3 * tinySlotSize, originY + i * tinySlotSize + tinySlotSize / 2);
        }
    }

    private void setup20Slots() {
        backgroundTexture = BACKGROUND_20_LOCATION;
        backgroundSize = 96;
        limitX = 44;
        limitY = 44;
        deadZone = 11;
        guiSlots = new GuiSlot[20];
        int originX = -tinySlotSize - (tinySlotSize / 2) - 2;
        int originY = -tinySlotSize - (tinySlotSize / 2) - 1 - 2;
        for (int i = 0; i < 3; i++) {
            guiSlots[i] = new GuiSlot(originX + i * tinySlotSize, originY);
            guiSlots[i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 2);
            if (i == 0 || i == 2) {
                guiSlots[i == 0 ? 6 : 7] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize);
            }
        }
        for (int i = 0; i < 3; i++) {
            guiSlots[8 + i] = new GuiSlot(originX + i * tinySlotSize, originY - tinySlotSize);
            guiSlots[8 + i + 3] = new GuiSlot(originX + i * tinySlotSize, originY + tinySlotSize * 3);
        }
        for (int i = 0; i < 3; i++) {
            guiSlots[14 + i] = new GuiSlot(originX - tinySlotSize, originY + i * tinySlotSize);
            guiSlots[14 + i + 3] = new GuiSlot(originX + tinySlotSize * 3, originY + i * tinySlotSize);
        }
    }

    private record GuiSlot(int x, int y) {
    }

}

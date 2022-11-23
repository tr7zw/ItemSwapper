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

public class SquareSwitchItemOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation BACKGROUND_3_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_3_row.png");
    private static final ResourceLocation BACKGROUND_4_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_4_nocenter.png");
    private static final ResourceLocation BACKGROUND_5_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_5_plus.png");
    private static final ResourceLocation BACKGROUND_6_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_6_nocenter.png");
    private static final ResourceLocation BACKGROUND_7_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_7_rows_half_aligned_bottom.png");
    private static final ResourceLocation BACKGROUND_8_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_8_nocenter.png");
    private static final ResourceLocation BACKGROUND_9_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_9.png");
    private static final ResourceLocation BACKGROUND_10_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_10_nocenter.png");
    private static final ResourceLocation BACKGROUND_12_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_12_nocenter.png");
    private static final ResourceLocation BACKGROUND_14_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_14.png");
    private static final ResourceLocation BACKGROUND_15_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_15_nocenter.png");
    private static final ResourceLocation BACKGROUND_16_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_16_nocenter.png");
    private static final ResourceLocation BACKGROUND_18_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_18_half_aligned.png");
    private static final ResourceLocation BACKGROUND_20_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_20_nocenter.png");
    private static final ResourceLocation BACKGROUND_22_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_22_half_aligned.png");
    private static final ResourceLocation BACKGROUND_24_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_24_nocenter.png");
    private static final ResourceLocation BACKGROUND_25_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inv_wheel_25_nocenter.png");
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
    private int backgroundSizeX = 0;
    private int backgroundSizeY = 0;
    private ResourceLocation backgroundTexture = null;
    public int globalYOffset = 0;
    public boolean forceAvailable = false;

    private double selectX = 0;
    private double selectY = 0;
    private int selection = -1;

    public SquareSwitchItemOverlay(Item[] selection, Item[] selectionSecondary) {
        this.itemSelection = selection;
        this.secondaryItemSelection = selectionSecondary;
        setupSlots();
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            forceAvailable = true;
        }
    }

    private void setupSlots() {
        if (itemSelection.length <= 3) {
            setupSlots(3, 1, false, BACKGROUND_3_LOCATION);
        } else if (itemSelection.length <= 4) {
            setupSlots(2, 2, false, BACKGROUND_4_LOCATION);
        } else if (itemSelection.length <= 5) {
            setupSlots(3, 3, true, BACKGROUND_5_LOCATION);
        } else if (itemSelection.length <= 6) {
            setupSlots(3, 2, false, BACKGROUND_6_LOCATION);
        } else if (itemSelection.length <= 7) {
            setupHalfGridSlots(4, 2, BACKGROUND_7_LOCATION);
        } else if (itemSelection.length <= 8) {
            setupSlots(4, 2, false, BACKGROUND_8_LOCATION);
        } else if (itemSelection.length <= 9) {
            setupSlots(3, 3, false, BACKGROUND_9_LOCATION);
        } else if (itemSelection.length <= 10) {
            setupSlots(5, 2, false, BACKGROUND_10_LOCATION);
        } else if (itemSelection.length <= 12) {
            setupSlots(4, 4, true, BACKGROUND_12_LOCATION);
        } else if (itemSelection.length <= 14) {
            setupSlots(6, 3, true, BACKGROUND_14_LOCATION);
        } else if (itemSelection.length <= 15) {
            setupSlots(5, 3, false, BACKGROUND_15_LOCATION);
        } else if (itemSelection.length <= 16) {
            setupSlots(4, 4, false, BACKGROUND_16_LOCATION);
        } else if (itemSelection.length <= 18) {
            setupHalfGridSlots(5, 4, BACKGROUND_18_LOCATION);
        } else if (itemSelection.length <= 20) {
            setupSlots(6, 4, true, BACKGROUND_20_LOCATION);
        } else if (itemSelection.length <= 22) {
            setupHalfGridSlots(6, 4, BACKGROUND_22_LOCATION);
        } else if (itemSelection.length <= 24) {
            setupSlots(6, 4, false, BACKGROUND_24_LOCATION);
        } else {
            setupSlots(5, 5, false, BACKGROUND_25_LOCATION);
        }
    }

    @Override
    public void render(PoseStack poseStack, int no1, int no2, float f) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (backgroundTexture != null) {
            RenderSystem.setShaderTexture(0, backgroundTexture);
            blit(poseStack, originX - (backgroundSizeX / 2), originY - (backgroundSizeY / 2), 0, 0, backgroundSizeX,
                    backgroundSizeY, 128, 128);
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

        if (!slots.isEmpty() && !forceAvailable) {
            itemRenderList.add(() -> renderSlot(x + 3, y + 4, minecraft.player, slots.get(0).item(), 1, false));
        } else if (id <= itemSelection.length - 1) {
            itemRenderList.add(
                    () -> renderSlot(x + 3, y + 4, minecraft.player, itemSelection[id].getDefaultInstance(), 1,
                            !forceAvailable));
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
        if (selection != -1 && selection < itemSelection.length && itemSelection[selection] != Items.AIR) {
            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
//                minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, 36 + minecraft.player.getInventory().selected);
                minecraft.gameMode.handleCreativeModeItemAdd(itemSelection[selection].getDefaultInstance().copy(),
                        36 + minecraft.player.getInventory().selected);
                return;
            }
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

    private void setupSlots(int width, int height, boolean skipCorners, ResourceLocation texture) {
        backgroundTexture = texture;
        backgroundSizeX = width * tinySlotSize + 6;
        backgroundSizeY = height * tinySlotSize + 6;
        int sz = texture == null ? slotSize : tinySlotSize;
        int lz = texture == null ? 11 : 9;
        limitX = width * lz;
        limitY = height * lz;
        deadZone = 1;
        int slotAmount = width * height - (skipCorners ? 4 : 0);
        guiSlots = new GuiSlot[slotAmount];
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = skipCorners
                        && ((x == 0 && y == 0) || (x == 0 && y == height - 1) || (x == width - 1 && y == height - 1)
                                || (x == width - 1 && y == 0));
                if (!skip) {
                    guiSlots[slotId++] = new GuiSlot(originX + x * sz, originY + y * sz);
                }
            }
        }
    }

    private void setupHalfGridSlots(int width, int height, ResourceLocation texture) {
        backgroundTexture = texture;
        backgroundSizeX = width * tinySlotSize + 6;
        backgroundSizeY = height * tinySlotSize + 6;
        int sz = texture == null ? slotSize : tinySlotSize;
        int lz = texture == null ? 11 : 9;
        limitX = width * lz;
        limitY = height * lz;
        deadZone = 1;
        int slotAmount = width * height - 2;
        if(height == 2)slotAmount++;
        guiSlots = new GuiSlot[slotAmount];
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = (x == width - 1 && y == height - 1)
                        || (x == width - 1 && y == 0);
                int xOffset = y == 0 || y == height - 1 ? sz / 2 : 0;
                if(height == 2 && y == 1) {
                    skip = false;
                    xOffset = 0;
                }
                if (!skip) {
                    guiSlots[slotId++] = new GuiSlot(originX + xOffset + x * sz, originY + y * sz);
                }
            }
        }
    }

    private record GuiSlot(int x, int y) {
    }

}

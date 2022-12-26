package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class SwitchItemOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation SELECTION_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/selection.png");
    
    public static final int slotSize = 22;
    public static final int tinySlotSize = 18;
    public final Minecraft minecraft = Minecraft.getInstance();
    public final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    public final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    public int globalXOffset = 0;
    public int globalYOffset = 0;
    public boolean forceAvailable = false;
    
    private final ConfigManager configManager = ConfigManager.getInstance();
    private double limitX = 33;
    private double limitY = 33;
    private double deadZone = 11;
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private ItemGroup itemGroup;
    private GuiSlot[] guiSlots;
    private int backgroundSizeX = 0;
    private int backgroundSizeY = 0;
    private int backgroundTextureSizeX = 128;
    private int backgroundTextureSizeY = 128;
    private ResourceLocation backgroundTexture = null;
    private double selectX = 0;
    private double selectY = 0;
    private int selection = -1;

    public SwitchItemOverlay(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;

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
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (getBackgroundTexture() != null) {
            RenderSystem.setShaderTexture(0, getBackgroundTexture());
            blit(poseStack, originX - (getBackgroundSizeX() / 2), originY - (getBackgroundSizeY() / 2), 0, 0, getBackgroundSizeX(),
                    getBackgroundSizeY(), backgroundTextureSizeX, backgroundTextureSizeY);
        }
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        List<Runnable> itemRenderList = new ArrayList<>();
        List<Runnable> lateRenderList = new ArrayList<>();
        for (int i = 0; i < getGuiSlots().length; i++) {
            renderSelection(poseStack, i, originX + getGuiSlots()[i].x, originY + getGuiSlots()[i].y, itemRenderList,
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

    public boolean forceItemsAvailable() {
        return forceAvailable;
    }
    
    public List<AvailableSlot> getItem(int id){
        return id > itemGroup.getItems().length - 1 ? Collections.emptyList()
                : providerManager.findSlotsMatchingItem(itemGroup.getItems()[id].getItem(), false, false);
    }
    
    private void renderSelection(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList,
            List<Runnable> lateRenderList) {
        if (getBackgroundTexture() == null) {
            blit(poseStack, x, y, 24, 22, 29, 24);
        }
        List<AvailableSlot> slots = getItem(id);
        if (getSelection() == id) {
            itemRenderList = lateRenderList;
            lateRenderList.add(() -> {
                float blit = getBlitOffset();
                setBlitOffset((int) this.itemRenderer.blitOffset);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, SELECTION_LOCATION);
                blit(poseStack, x-1, y, 0, 0, 24, 24, 24, 24);
                setBlitOffset((int) blit);
            });
        }
        if (!slots.isEmpty() && !forceItemsAvailable()) {
            itemRenderList.add(() -> renderSlot(poseStack, x + 3, y + 4, minecraft.player, slots.get(0).item(), 1, false, slots.get(0).amount().get()));
            if (getSelection() == id)
                itemRenderList.add(() -> renderSelectedItemName(slots.get(0).item(), false));

        } else if (id <= itemGroup.getItems().length - 1) {
            itemRenderList.add(
                    () -> renderSlot(poseStack, x + 3, y + 4, minecraft.player, itemGroup.getItems()[id].getItem().getDefaultInstance(), 1,
                            !forceItemsAvailable(), 1));
            if (getSelection() == id)
                itemRenderList.add(() -> renderSelectedItemName(itemGroup.getItems()[id].getItem().getDefaultInstance(), !forceItemsAvailable()));
        }
    }

    public void handleInput(double x, double y) {
        selectX += x;
        selectY += y;
        selectX = Mth.clamp(selectX, -getLimitX(), getLimitX());
        selectY = Mth.clamp(selectY, -getLimitY(), getLimitY());
        updateSelection();
    }

    public void handleSwitchSelection() {
        // Don't allow switching if there is no second set
        if (getSelection() != -1 && getSelection() < itemGroup.getItems().length && itemGroup.getItems()[getSelection()].getItem() != Items.AIR) {
            ItemGroup sel = ItemSwapperMod.instance.getItemGroupManager().getNextPage(itemGroup, itemGroup.getItems()[getSelection()]);
            if(sel != null) {
                this.itemGroup = sel;
            }
        }
        setupSlots();
    }

    private void updateSelection() {
        selection = -1;
        double centerDist = Math.sqrt(selectX * selectX + selectY * selectY);
        if (centerDist < getDeadZone()) {
            return;
        }
        double best = Double.MAX_VALUE;
        int halfSlot = slotSize / 2;
        for (int i = 0; i < getGuiSlots().length; i++) {
            double mouseDist = Math.sqrt((selectX - getGuiSlots()[i].x - halfSlot) * (selectX - getGuiSlots()[i].x - halfSlot)
                    + (selectY - getGuiSlots()[i].y - halfSlot) * (selectY - getGuiSlots()[i].y - halfSlot));
            if (mouseDist < best) {
                best = mouseDist;
                selection = i;
            }
        }
    }

    public void onClose() {
        if (getSelection() != -1 && getSelection() < itemGroup.getItems().length && itemGroup.getItems()[getSelection()].getItem() != Items.AIR) {
            if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
//                minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, 36 + minecraft.player.getInventory().selected);
                minecraft.gameMode.handleCreativeModeItemAdd(itemGroup.getItems()[getSelection()].getItem().getDefaultInstance().copy(),
                        36 + minecraft.player.getInventory().selected);
                return;
            }
            List<AvailableSlot> slots = providerManager.findSlotsMatchingItem(itemGroup.getItems()[getSelection()].getItem(), true, false);
            if (!slots.isEmpty()) {
                AvailableSlot slot = slots.get(0);
                OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
                if(event.canceled().get()) {
                    // interaction canceled by some other mod
                    return;
                }
                if (slot.inventory() == -1) {
                    int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                    this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                            hudSlot, minecraft.player.getInventory().selected,
                            ClickType.SWAP, this.minecraft.player);
                } else {
                    NetworkUtil.swapItem(slot.inventory(), slot.slot());
                }
                clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
            }
        }
    }

    private void renderSlot(PoseStack poseStack, int x, int y, Player arg, ItemStack arg2, int k, boolean grayOut, int count) {
        if (!arg2.isEmpty()) {
            ItemStack copy = arg2.copy();
            copy.setCount(1);
            if (grayOut) {
                RenderHelper.renderUnavailableItem(poseStack, arg, copy, x, y, k);
                return;
            }
            this.itemRenderer.renderAndDecorateItem(arg, copy, x, y, k);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, copy, x, y);
            int color = count > 64 ? 0xFFFF00 : 0xFFFFFF;
            if(count > 1)
                RenderHelper.renderGuiItemCount(minecraft.font, ""+Math.min(64, count), x, y, color);
        }
    }

    private void renderSelectedItemName(ItemStack arg2, boolean grayOut) {
        if (!arg2.isEmpty()) {
            int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
            int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;
            TextColor textColor = arg2.getHoverName().getStyle().getColor();
            ChatFormatting rarityColor = arg2.getRarity().color;
            int color = 0xFFFFFF;
            if(grayOut) {
                color = 0xAAAAAA;
            } else if(textColor != null) {
                color = textColor.getValue();
            } else if(rarityColor != null && rarityColor.getColor() != null) {
                color = rarityColor.getColor();
            }
            RenderHelper.renderGuiItemName(minecraft.font, arg2.getHoverName().getString(), originX, originY - (getBackgroundSizeY() / 2) - 12, color);
        }
    }

    public void setupSlots(int width, int height, boolean skipCorners, ResourceLocation texture) {
        setBackgroundTexture(texture);
        setBackgroundSizeX(width * tinySlotSize + 6);
        setBackgroundSizeY(height * tinySlotSize + 6);
        int sz = texture == null ? slotSize : tinySlotSize;
        int lz = texture == null ? 11 : 9;
        setLimitX(width * lz);
        setLimitY(height * lz);
        setDeadZone(1);
        int slotAmount = width * height - (skipCorners ? 4 : 0);
        setGuiSlots(new GuiSlot[slotAmount]);
        int originX = (int) (-width / 2d * sz - 2);
        int originY = (int) (-height / 2d * sz - 1 - 2);
        int slotId = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean skip = skipCorners
                        && ((x == 0 && y == 0) || (x == 0 && y == height - 1) || (x == width - 1 && y == height - 1)
                                || (x == width - 1 && y == 0));
                if (!skip) {
                    getGuiSlots()[slotId++] = new GuiSlot(originX + x * sz, originY + y * sz);
                }
            }
        }
    }

    public ResourceLocation getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public int getBackgroundSizeX() {
        return backgroundSizeX;
    }

    public void setBackgroundSizeX(int backgroundSizeX) {
        this.backgroundSizeX = backgroundSizeX;
    }

    public int getBackgroundSizeY() {
        return backgroundSizeY;
    }

    public void setBackgroundSizeY(int backgroundSizeY) {
        this.backgroundSizeY = backgroundSizeY;
    }

    public double getLimitX() {
        return limitX;
    }

    public void setLimitX(double limitX) {
        this.limitX = limitX;
    }

    public double getLimitY() {
        return limitY;
    }

    public void setLimitY(double limitY) {
        this.limitY = limitY;
    }

    public double getDeadZone() {
        return deadZone;
    }

    public void setDeadZone(double deadZone) {
        this.deadZone = deadZone;
    }

    public GuiSlot[] getGuiSlots() {
        return guiSlots;
    }

    public void setGuiSlots(GuiSlot[] guiSlots) {
        this.guiSlots = guiSlots;
    }
    
    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public int getSelection() {
        return selection;
    }

    public int getBackgroundTextureSizeX() {
        return backgroundTextureSizeX;
    }

    public void setBackgroundTextureSizeX(int backgroundTextureSizeX) {
        this.backgroundTextureSizeX = backgroundTextureSizeX;
    }

    public int getBackgroundTextureSizeY() {
        return backgroundTextureSizeY;
    }

    public void setBackgroundTextureSizeY(int backgroundTextureSizeY) {
        this.backgroundTextureSizeY = backgroundTextureSizeY;
    }

    public record GuiSlot(int x, int y) {
    }

}

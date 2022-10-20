package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.NetworkLogic;
import dev.tr7zw.itemswapper.util.ItemUtil.Slot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemListOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final double entrySize = 33;
    private static final int yOffset = 52;
    private static final int slotSize = 22;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private Item[] itemSelection;
    private List<Slot> entries = new ArrayList<>();
    private int selectedEntry = 0;
    private double selectY = 0;

    public ItemListOverlay(Item[] itemSelection) {
        this.itemSelection = itemSelection;
        refreshList();
    }

    @Override
    public void render(PoseStack poseStack, int paramInt1, int paramInt2, float paramFloat) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        List<Runnable> itemRenderList = new ArrayList<>();
        int limit = Math.max(5, (minecraft.getWindow().getGuiScaledHeight() - yOffset) / slotSize / 2);
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 - slotSize * 3;
        int originY = minecraft.getWindow().getGuiScaledHeight() - yOffset + (Math.max(0, selectedEntry - limit/2) * slotSize);
        int start = Math.max(0, selectedEntry - limit/2);
        for (int i = start; i < entries.size() && i < start + limit; i++) {
            renderEntry(poseStack, i, originX, originY - slotSize * i, itemRenderList);
        }
        itemRenderList.forEach(Runnable::run);
    }

    @Override
    public void handleInput(double x, double y) {
        selectY -= y;
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize - 1);
        refreshList();
    }
    
    @Override
    public void onScroll(double signum) {
        selectY += signum * entrySize;
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize - 1);
        refreshList();
    }

    private void refreshList() {
        entries.clear();
        // first slot is always the current item
        entries.add(new Slot(-1, minecraft.player.getInventory().selected, minecraft.player.getInventory().getSelected()));
        for (Item item : itemSelection) {
            List<Slot> ids = ItemUtil.findSlotsMatchingItem(item);
            for (Slot id : ids) {
                if (!entries.contains(id)) {
                    entries.add(id);
                }
            }
        }
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize - 1);
        selectedEntry = (int) (selectY / entrySize);
    }

    @Override
    public void handleSwitchSelection() {

    }

    @Override
    public void onClose() {
        if (selectedEntry != 0) {
            Slot slot = entries.get(selectedEntry);
            if(slot.inventory() == -1) {
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot,
                        minecraft.player.getInventory().selected,
                        ClickType.SWAP, this.minecraft.player);
            } else {
                NetworkLogic.swapItem(slot.inventory(), slot.slot());
            }
        }
    }

    private void renderEntry(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList) {
        blit(poseStack, x, y, 24, 22, 29, 24);
        // dummy item code
        Slot slot = entries.get(id);
        itemRenderList.add(() -> {
            renderSlot(x + 3, y + 4, minecraft.player, slot.item(), 1);
            drawString(poseStack, minecraft.font, slot.item().getHoverName(),
                    x + 25, y + 11, -1);
        });
        if (selectedEntry == id) {
            blit(poseStack, x-1, y, 0, 22, 24, 24);
        }
    }

    private void renderSlot(int x, int y, Player arg, ItemStack arg2, int k) {
        if (!arg2.isEmpty()) {
            this.itemRenderer.renderAndDecorateItem(arg, arg2, x, y, k);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, arg2, x, y);
        }
    }

}

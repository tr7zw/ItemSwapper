package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemListOverlay extends XTOverlay {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final double entrySize = 33;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private Item[] itemSelection;
    private List<Integer> entries = new ArrayList<>();
    private int selectedEntry = 0;
    private double selectY = 0;
    
    public ItemListOverlay(Item[] itemSelection) {
        this.itemSelection = itemSelection;
        refreshList();
    }
    
    @Override
    public void render(PoseStack paramPoseStack, int paramInt1, int paramInt2, float paramFloat) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        List<Runnable> itemRenderList = new ArrayList<>();
        int slotSize = 22;
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 - slotSize*3;
        int originY = minecraft.getWindow().getGuiScaledHeight() - 52;
        for(int i = 0; i < entries.size(); i++) {
            renderEntry(paramPoseStack, i, originX, originY - slotSize * i, itemRenderList);
        }
        itemRenderList.forEach(Runnable::run);
    }
    
    @Override
    public void handleInput(double x, double y) {
        selectY -= y;
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize-1);
        refreshList();
    }
    
    private void refreshList() {
        entries.clear();
        // first slot is always the current item
        entries.add(minecraft.player.getInventory().selected);
        for(Item item : itemSelection) {
            List<Integer> ids = findSlotsMatchingItem(item);
            for(Integer id : ids) {
                if(!entries.contains(id)) {
                    entries.add(id);
                }
            }
        }
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize-1);
        selectedEntry = (int) (selectY / entrySize);
    }

    @Override
    public void handleSwitchSelection() {
        
    }
    
    @Override
    public void onClose() {
        if(selectedEntry != 0) {
            int inventorySlot = entries.get(selectedEntry);
            if(inventorySlot != -1) {
                int hudSlot = inventorySlotToHudSlot(inventorySlot);
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot, minecraft.player.getInventory().selected,
                        ClickType.SWAP, this.minecraft.player);
            }
        }
    }
    
    private List<Integer> findSlotsMatchingItem(Item item) {
        NonNullList<ItemStack> items = minecraft.player.getInventory().items;
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!(items.get(i)).isEmpty()
                    && items.get(i).getItem() == item)
                ids.add(i);
        }
        return ids;
    }
    
    private int inventorySlotToHudSlot(int slot) {
        if(slot < 9) {
            return 36+slot;
        }
        return slot;
    }
    
    
    private void renderEntry(PoseStack poseStack, int id, int x, int y, List<Runnable> itemRenderList) {
        blit(poseStack, x, y, 24, 22, 29, 24);
        //dummy item code
        int slot = entries.get(id);
        if(slot != -1) {
            itemRenderList.add(() -> {
                renderSlot(x+3, y+3, minecraft.player, minecraft.player.getInventory().getItem(slot), 1);
                drawString(poseStack, minecraft.font, minecraft.player.getInventory().getItem(slot).getHoverName(), x+25, y+11, -1);
            });
        }
        if(selectedEntry == id) {
            blit(poseStack, x, y, 0, 22, 24, 22);
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

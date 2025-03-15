package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.blaze3d.systems.RenderSystem;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.trender.gui.client.RenderContext;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class ItemListOverlay extends ItemSwapperUIAbstractInput {
    private static final ResourceLocation SELECTION_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/selection.png");
    private static final ResourceLocation BOTTOM_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_bottom_slot.png");
    private static final ResourceLocation MIDDLE_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_slot.png");
    private static final ResourceLocation TOP_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_top_slot.png");
    private static final ResourceLocation SINGLE_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_single_slot.png");
    private static final ResourceLocation MIDDLE_TOP_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_continue_top_slot.png");
    private static final ResourceLocation MIDDLE_BOTTOM_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_continue_bottom_slot.png");

    private static final double entrySize = 33;
    private static final int yOffset = 75;
    private static final int slotSize = 18;
    private final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    private ItemList itemSelection;
    private List<AvailableSlot> entries = new ArrayList<>();
    private int selectedEntry = 0;
    private double selectY = 0;

    public ItemListOverlay(ItemList itemSelection) {
        super(ComponentProvider.empty());
        this.minecraft = Minecraft.getInstance();
        this.itemSelection = itemSelection;
        refreshList();
    }

    @Override
    //#if MC >= 12000
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float f) {
        RenderContext renderContext = new RenderContext(graphics);
        //#else
        //$$ public void render(PoseStack pose, int mouseX, int mouseY, float f) {
        //$$ RenderContext renderContext = new RenderContext(this, pose);
        //#endif
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //#if MC >= 12102
        RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX);
        //#else
        //$$ RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        //#endif
        List<Runnable> itemRenderList = new ArrayList<>();
        List<Runnable> lateRenderList = new ArrayList<>();
        int limit = Math.max(5, (minecraft.getWindow().getGuiScaledHeight() - yOffset) / slotSize / 2);
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 - slotSize * 5;
        int originY = minecraft.getWindow().getGuiScaledHeight() - yOffset
                + (Math.max(0, selectedEntry - limit / 2) * slotSize);
        int start = Math.max(0, selectedEntry - limit / 2);
        for (int i = start; i < entries.size() && i < start + limit; i++) {
            boolean endTop = i == entries.size() - 1;
            boolean endBottom = i == 0;
            boolean midBottom = i == start;
            boolean midTop = i == start + limit - 1;
            ResourceLocation background = MIDDLE_LOCATION;
            if (endTop && endBottom) {
                background = SINGLE_LOCATION;
            } else if (endBottom) {
                background = BOTTOM_LOCATION;
            } else if (endTop) {
                background = TOP_LOCATION;
            } else if (midTop) {
                background = MIDDLE_TOP_LOCATION;
            } else if (midBottom) {
                background = MIDDLE_BOTTOM_LOCATION;
            }
            renderEntry(renderContext, background, i, originX, originY - slotSize * i, itemRenderList, lateRenderList);
        }
        itemRenderList.forEach(Runnable::run);
        lateRenderList.forEach(Runnable::run);
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
        entries.add(new AvailableSlot(-1, minecraft.player.getInventory().selected,
                minecraft.player.getInventory().getSelected()));
        for (Item item : itemSelection.getItems()) {
            List<AvailableSlot> ids = providerManager.findSlotsMatchingItem(item, false,
                    ConfigManager.getInstance().getConfig().ignoreHotbar);
            for (AvailableSlot id : ids) {
                if (!entries.contains(id)) {
                    entries.add(id);
                }
            }
        }
        selectY = Mth.clamp(selectY, 0, entries.size() * entrySize - 1);
        selectedEntry = (int) (selectY / entrySize);
    }

    @Override
    public void onSecondaryClick() {
        if (this.itemSelection.getLink() != null) {
            Page linkedPage = ItemSwapperSharedMod.instance.getItemGroupManager().getPage(this.itemSelection.getLink());
            if (linkedPage != null && linkedPage instanceof ListPage listPage) {
                this.itemSelection = listPage.items();
                selectY = 0;
                refreshList();
            }
        }
    }

    @Override
    public boolean lockMouse() {
        return !ConfigManager.getInstance().getConfig().unlockListMouse;
    }

    @Override
    public boolean onPrimaryClick() {
        if (selectedEntry != 0) {
            AvailableSlot slot = entries.get(selectedEntry);
            OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
            if (event.canceled().get()) {
                // interaction canceled by some other mod
                return true;
            }
            if (slot.inventory() == -1) {
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot,
                        minecraft.player.getInventory().selected, ClickType.SWAP, this.minecraft.player);
            } else {
                NetworkUtil.swapItem(slot.inventory(), slot.slot());
            }
            clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
        }
        return false;
    }

    private void renderEntry(RenderContext graphics, ResourceLocation background, int id, int x, int y,
            List<Runnable> itemRenderList, List<Runnable> lateRenderList) {
        graphics.blit(background, x, y, 0, 0, 24, 24, 24, 24);
        // dummy item code
        AvailableSlot slot = entries.get(id);
        if (selectedEntry == id) {
            lateRenderList.add(() -> {
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, RenderHelper.LAYERS_SELECTION);
                graphics.blit(SELECTION_LOCATION, x, y, 0, 0, 24, 24, 24, 24);
                graphics.pose().popPose();
            });
        }
        lateRenderList.add(() -> {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, RenderHelper.LAYERS_ITEM);
            renderSlot(graphics, x + 4, y + 4, minecraft.player, slot.item(), 1);
            graphics.pose().popPose();
            var name = ItemUtil.getDisplayname(slot.item());
            if (selectedEntry != id && name instanceof MutableComponent mutName) {
                mutName.withStyle(ChatFormatting.GRAY);
            }
            graphics.drawString(minecraft.font, name, x + 27, y + 9, -1);
        });
    }

    private void renderSlot(RenderContext graphics, int x, int y, Player arg, ItemStack arg2, int k) {
        if (!arg2.isEmpty()) {
            graphics.renderItem(arg, arg2, x, y, k);
            //#if MC >= 12102
            RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_COLOR);
            //#else
            //$$ RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionColorShader);
            //#endif
            graphics.renderItemDecorations(this.minecraft.font, arg2, x, y);
        }
    }

}

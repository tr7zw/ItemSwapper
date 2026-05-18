package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.List;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.manager.*;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.packets.serverbound.*;
import dev.tr7zw.transition.loader.networking.*;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.InventoryUtil;
import dev.tr7zw.trender.gui.client.RenderContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//? if >= 1.20.0 {

import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else {
/*
import com.mojang.blaze3d.vertex.PoseStack;
*///? }

public class ItemListOverlay extends ItemSwapperUIAbstractInput {
    private static final Identifier SELECTION_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/selection.png");
    private static final Identifier BOTTOM_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_bottom_slot.png");
    private static final Identifier MIDDLE_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_slot.png");
    private static final Identifier TOP_LOCATION = getResourceLocation("itemswapper", "textures/gui/list_top_slot.png");
    private static final Identifier SINGLE_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_single_slot.png");
    private static final Identifier MIDDLE_TOP_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_continue_top_slot.png");
    private static final Identifier MIDDLE_BOTTOM_LOCATION = getResourceLocation("itemswapper",
            "textures/gui/list_middle_continue_bottom_slot.png");

    private static final double entrySize = 33;
    private static final int yOffset = 75;
    private static final int slotSize = 18;
    private final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    private final ItemManager itemManager = ItemSwapperSharedMod.instance.getItemManager();
    private final Minecraft minecraft = Minecraft.getInstance();
    private ItemList itemSelection;
    private List<AvailableSlot> entries = new ArrayList<>();
    private int selectedEntry = 0;
    private double selectY = 0;

    public ItemListOverlay(ItemList itemSelection) {
        super(ComponentProvider.empty());
        this.itemSelection = itemSelection;
        refreshList();
    }

    @Override
    //? if >= 26.1 {

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float f) {
        RenderContext renderContext = new RenderContext(graphics);
        //? } else if >= 1.20.0 {

        /*public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float f) {
        RenderContext renderContext = new RenderContext(graphics);
        *///? } else {
        /*
            public void render(PoseStack pose, int mouseX, int mouseY, float f) {
        RenderContext renderContext = new RenderContext(this, pose);
        *///? }
           //        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
           //? if < 1.21.6 {

        /*com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///? }
           //? if >= 1.21.5 {

        //? } else if >= 1.21.2 {

        /*com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX);
        *///? } else {

        /*com.mojang.blaze3d.systems.RenderSystem
                .setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        *///? }
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
            Identifier background = MIDDLE_LOCATION;
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
        entries.add(new AvailableSlot(-1, InventoryUtil.getSelectedId(minecraft.player.getInventory()),
                InventoryUtil.getSelected(minecraft.player.getInventory())));
        for (Item item : itemSelection.getItems()) {
            List<AvailableSlot> ids = providerManager.findSlotsMatchingItem(item, false,
                    ConfigHolder.getInstance().getGeneral().getConfig().ignoreHotbar);
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
        return !ConfigHolder.getInstance().getGeneral().getConfig().unlockListMouse;
    }

    @Override
    public boolean onPrimaryClick() {
        if (selectedEntry != 0) {
            AvailableSlot slot = entries.get(selectedEntry);
            itemManager.grabItem(slot);
        }
        return false;
    }

    private void renderEntry(RenderContext graphics, Identifier background, int id, int x, int y,
            List<Runnable> itemRenderList, List<Runnable> lateRenderList) {
        graphics.blit(background, x, y, 0, 0, 24, 24, 24, 24);
        // dummy item code
        AvailableSlot slot = entries.get(id);
        if (selectedEntry == id) {
            lateRenderList.add(() -> {
                //? if < 1.21.6 {

                /*graphics.getPose().pushPose();
                graphics.getPose().translate(0, 0, dev.tr7zw.itemswapper.util.RenderHelper.LAYERS_SELECTION);
                *///? }
                graphics.blit(SELECTION_LOCATION, x, y, 0, 0, 24, 24, 24, 24);
                //? if < 1.21.6 {

                /*graphics.getPose().popPose();
                *///? }
            });
        }
        lateRenderList.add(() -> {
            //? if < 1.21.6 {

            /*graphics.getPose().pushPose();
            graphics.getPose().translate(0, 0, dev.tr7zw.itemswapper.util.RenderHelper.LAYERS_ITEM);
            *///? }
            renderSlot(graphics, x + 4, y + 4, minecraft.player, slot.item(), 1);
            //? if < 1.21.6 {

            /*graphics.getPose().popPose();
            *///? }
            var name = ItemSwapperSharedMod.instance.getItemManager().getDisplayname(slot.item());
            if (selectedEntry != id && name instanceof MutableComponent mutName) {
                mutName.withStyle(ChatFormatting.GRAY);
            }
            graphics.drawString(minecraft.font, name, x + 27, y + 9, -1);
        });
    }

    private void renderSlot(RenderContext graphics, int x, int y, Player arg, ItemStack arg2, int k) {
        if (!arg2.isEmpty()) {
            graphics.renderItem(arg, arg2, x, y, k);
            //? if >= 1.21.5 {

            //? } else if >= 1.21.2 {

            /*com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_COLOR);
            *///? } else {

            /*com.mojang.blaze3d.systems.RenderSystem
                    .setShader(net.minecraft.client.renderer.GameRenderer::getPositionColorShader);
            *///? }
            graphics.renderItemDecorations(this.minecraft.font, arg2, x, y);
        }
    }

}

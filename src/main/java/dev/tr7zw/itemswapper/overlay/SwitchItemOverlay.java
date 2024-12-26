package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.ItemSwapperUI;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.compat.ControlifySupport;
import dev.tr7zw.itemswapper.compat.ViveCraftSupport;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ContainerPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.InventoryPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.NoPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.TexturePage;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.BackShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.BlockColorShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.ClearCurrentSlotShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LastItemShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LinkShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.OpenInventoryShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.RestockShortcut;
import dev.tr7zw.itemswapper.overlay.logic.BlockListWidget;
import dev.tr7zw.itemswapper.overlay.logic.ContainerWidget;
import dev.tr7zw.itemswapper.overlay.logic.GuiSelectionHandler;
import dev.tr7zw.itemswapper.overlay.logic.GuiWidget;
import dev.tr7zw.itemswapper.overlay.logic.HotbarWidget;
import dev.tr7zw.itemswapper.overlay.logic.InventoryWidget;
import dev.tr7zw.itemswapper.overlay.logic.ListContentWidget;
import dev.tr7zw.itemswapper.overlay.logic.PaletteWidget;
import dev.tr7zw.itemswapper.overlay.logic.ShortcutListWidget;
import dev.tr7zw.itemswapper.util.ColorUtil.UnpackedColor;
import dev.tr7zw.itemswapper.util.RenderHelper;
import dev.tr7zw.itemswapper.util.WidgetUtil;
import dev.tr7zw.util.ComponentProvider;
import dev.tr7zw.util.RenderContext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;

//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class SwitchItemOverlay extends ItemSwapperUIAbstractInput {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    private final GuiSelectionHandler selectionHandler = new GuiSelectionHandler();
    @Setter
    private int globalXOffset = 0;
    private int globalYOffset = 0;
    @Setter
    private boolean forceAvailable = false;
    @Setter
    private boolean hideCursor = false;
    @Setter
    private boolean hideShortcuts = false;
    private List<Shortcut> shortcutList = Collections.emptyList();
    @Getter
    private List<Page> lastPages = new ArrayList<>();
    @Setter
    private boolean hideClearSlotShortcut = false;

    private final ConfigManager configManager = ConfigManager.getInstance();

    private SwitchItemOverlay() {
        super(ComponentProvider.empty());
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            setForceAvailable(true);
        }
    }

    private void initShortcuts() {
        shortcutList = new ArrayList<>();
        if (hideShortcuts) {
            return;
        }
        if (!hideClearSlotShortcut) {
            shortcutList.add(new ClearCurrentSlotShortcut());
        }
        shortcutList.add(new LastItemShortcut(ItemSwapperSharedMod.instance.getLastItem(),
                ItemSwapperSharedMod.instance.getLastPage()));
        if (configManager.getConfig().experimentalAutoPalette) {
            shortcutList.add(new BlockColorShortcut(null, 0));
        }
        if (ItemSwapperSharedMod.instance.isEnableRefill()) {
            shortcutList.add(new RestockShortcut());
        }
        if (configManager.getConfig().showOpenInventoryButton) {
            shortcutList.add(new OpenInventoryShortcut(this));
        }
        shortcutList.add(new BackShortcut(this));
        shortcutList.add(new LinkShortcut(getResourceLocation("itemswapper", "v2/main"),
                ComponentProvider.translatable("text.itemswapper.overview"), null));
    }

    public static SwitchItemOverlay createPageOverlay(Page page) {
        SwitchItemOverlay overlay = new SwitchItemOverlay();
        overlay.openPage(page);
        return overlay;
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
        lastPages.add(new ItemGroupPage(itemGroup));
        initShortcuts();
        GuiWidget mainWidget = new PaletteWidget(itemGroup, 0, 0);
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(itemGroup.getId(), shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 1));
        selectionHandler.addWidget(new ShortcutListWidget(itemGroup.getId(), itemGroup.getShortcuts(),
                -mainWidget.getWidgetArea().getMouseBoundsX() - ItemSwapperUI.slotSize, 1));
    }

    public void openItemList(ItemList items) {
        selectionHandler.reset();
        lastPages.add(new ListPage(items));
        initShortcuts();
        GuiWidget mainWidget = new ListContentWidget(items, 0, 0);
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(items.getId(), shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 1));
    }

    public boolean openPage(Page page) {
        if (page instanceof NoPage || (!lastPages.isEmpty() && page.equals(lastPages.get(lastPages.size() - 1)))) {
            return false; // this exact page is already open
        }
        switch (page) {
        case ItemGroupPage(var gr) -> openItemGroup(gr);
        case ListPage list -> openItemList(list.items());
        case InventoryPage inv -> openInventory();
        case ContainerPage(int id) -> openContainer(id);
        case TexturePage(var color, var sideBase) -> openTexturePallete(color, sideBase);
        case NoPage noPage -> throw new RuntimeException("Unexpected value: " + page);
        }
        return true;
    }

    public boolean selectIcon(String selector, int xOffset, int yOffset) {
        return selectionHandler.select(selector, xOffset, yOffset, this);
    }

    public void openInventory() {
        selectionHandler.reset();
        lastPages.add(new InventoryPage());
        initShortcuts();
        InventoryWidget mainWidget = new InventoryWidget(0, 0);
        selectionHandler.addWidget(mainWidget);
        if (configManager.getConfig().showHotbar) {
            HotbarWidget hotbarWidget = new HotbarWidget(0,
                    mainWidget.getWidgetArea().getMouseBoundsY() + ItemSwapperUI.slotSize);
            selectionHandler.addWidget(hotbarWidget);
        }
        selectionHandler.addWidget(new ShortcutListWidget(null, shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
    }

    public void openContainer(int slotId) {
        // Check that this is valid
        ItemStack item = minecraft.player.getInventory().items.get(slotId);
        ContainerProvider provider = providerManager.getContainerProvider(item.getItem());
        if (provider == null) {
            // fallback, reset the UI and open the inventory
            lastPages.clear();
            openInventory();
            return;
        }
        selectionHandler.reset();
        lastPages.add(new ContainerPage(slotId));
        initShortcuts();
        ContainerWidget mainWidget = new ContainerWidget(0, 0, slotId);
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(null, shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
    }

    public void openTexturePallete(UnpackedColor[] color, UnpackedColor sideBase) {
        selectionHandler.reset();
        lastPages.add(new TexturePage(color, sideBase));
        initShortcuts();
        List<Shortcut> leftList = new ArrayList<>();
        leftList.add(new BlockColorShortcut(sideBase, 0));
        leftList.add(new BlockColorShortcut(sideBase, 1));
        leftList.add(new BlockColorShortcut(sideBase, 2));
        leftList.add(new BlockColorShortcut(sideBase, 3));
        BlockListWidget mainWidget = new BlockListWidget(0, 0,
                ItemSwapperSharedMod.instance.getBlockTextureManager().getBlocksByAverageColor(color));
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(null, shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
        selectionHandler.addWidget(new ShortcutListWidget(null, leftList,
                -mainWidget.getWidgetArea().getMouseBoundsX() - ItemSwapperUI.slotSize, 1));
    }

    @Override
    //#if MC >= 12000
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float f) {
        RenderContext renderContext = new RenderContext(graphics);
        //#else
        //$$ public void render(PoseStack pose, int mouseX, int mouseY, float f) {
        //$$ RenderContext renderContext = new RenderContext(this, pose);
        //#endif
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 + globalXOffset;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;

        // Pass raw mouse position to the selection handler for ViveCraft compatibility
        selectionHandler.updateMousePosition(mouseX - originX, mouseY - originY);

        for (GuiWidget widget : selectionHandler.getWidgets()) {
            widget.render(this, renderContext, originX, originY, forceAvailable);
        }
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().renderSelectedSlotName(selectionHandler.getSelectedSlot(),
                    selectionHandler.getWidgets().get(0).titleYOffset(),
                    selectionHandler.getWidgets().get(0).getWidgetArea().getBackgroundTextureSizeX() - 40,
                    forceAvailable, renderContext);
            if (configManager.getConfig().showTooltips) {
                selectionHandler.getSelectedWidget().renderSelectedTooltip(this, renderContext,
                        selectionHandler.getSelectedSlot(), selectionHandler.getCursorX() + originX,
                        selectionHandler.getCursorY() + originY);
            }
        }

        if (configManager.getConfig().showCursor && !hideCursor && !ViveCraftSupport.getInstance().isActive()
                && !ControlifySupport.getInstance().isActive(this)) {
            //#if MC >= 12102
            RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX);
            //#else
            //$$ RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
            //#endif
            renderContext.pose().pushPose();
            renderContext.pose().translate(0, 0, RenderHelper.LAYERS_CURSOR);
            renderContext.blit(WidgetUtil.CURSOR_LOCATION, originX + (int) selectionHandler.getCursorX() - 12,
                    originY + (int) selectionHandler.getCursorY() - 12, 0, 0, 24, 24, 24, 24);
            renderContext.pose().popPose();
        }
    }

    public void handleInput(double x, double y) {
        selectionHandler.updateSelection(x, y, this);
    }

    public void onSecondaryClick() {
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onSecondaryClick(this, selectionHandler.getSelectedSlot(),
                    (int) selectionHandler.getOffsetX(), (int) selectionHandler.getOffsetY());
        }
    }

    public boolean onPrimaryClick() {
        if (selectionHandler.getSelectedSlot() != null) {
            return selectionHandler.getSelectedWidget().onPrimaryClick(this, selectionHandler.getSelectedSlot(),
                    (int) selectionHandler.getOffsetX(), (int) selectionHandler.getOffsetY());
        }
        return true;
    }

}

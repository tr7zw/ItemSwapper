package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ContainerPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.InventoryPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.NoPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.BackShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.ClearCurrentSlotShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LastItemShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.LinkShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.OpenInventoryShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.RestockShortcut;
import dev.tr7zw.itemswapper.overlay.logic.ContainerWidget;
import dev.tr7zw.itemswapper.overlay.logic.GuiSelectionHandler;
import dev.tr7zw.itemswapper.overlay.logic.GuiWidget;
import dev.tr7zw.itemswapper.overlay.logic.InventoryWidget;
import dev.tr7zw.itemswapper.overlay.logic.ListContentWidget;
import dev.tr7zw.itemswapper.overlay.logic.PaletteWidget;
import dev.tr7zw.itemswapper.overlay.logic.ShortcutListWidget;
import dev.tr7zw.itemswapper.util.ViveCraftSupport;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SwitchItemOverlay extends ItemSwapperUIAbstractInput {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    public final Minecraft minecraft = Minecraft.getInstance();
    public final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    public final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final GuiSelectionHandler selectionHandler = new GuiSelectionHandler();
    public int globalXOffset = 0;
    public int globalYOffset = 0;
    public boolean forceAvailable = false;
    public boolean hideCursor = false;
    public boolean hideShortcuts = false;
    private List<Shortcut> shortcutList = Collections.emptyList();
    private List<Page> lastPages = new ArrayList<>();
    public boolean hideClearSlotShortcut = false;

    private final ConfigManager configManager = ConfigManager.getInstance();

    private SwitchItemOverlay() {
        super(ComponentProvider.empty());
        super.passEvents = true;
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            forceAvailable = true;
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
        shortcutList.add(new LastItemShortcut(ItemSwapperSharedMod.instance.getLastItem(), ItemSwapperSharedMod.instance.getLastPage()));
        if (ItemSwapperSharedMod.instance.isEnableRefill()) {
            shortcutList.add(new RestockShortcut());
        }
        shortcutList.add(new OpenInventoryShortcut(this));
        shortcutList.add(new BackShortcut(this));
        shortcutList.add(new LinkShortcut(new ResourceLocation("itemswapper", "v2/main"), ComponentProvider.translatable("text.itemswapper.overview"), null));
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
        if (page instanceof ItemGroupPage group) {
            openItemGroup(group.group());
        } else if (page instanceof ListPage list) {
            openItemList(list.items());
        } else if (page instanceof InventoryPage) {
            openInventory();
        } else if (page instanceof ContainerPage container) {
            openContainer(container.containerSlotId());
        }
        return true;
    }

    public boolean selectIcon(String selector, int xOffset, int yOffset) {
        return selectionHandler.select(selector, xOffset, yOffset);
    }

    public List<Page> getPageHistory() {
        return lastPages;
    }

    public void openInventory() {
        selectionHandler.reset();
        lastPages.add(new InventoryPage());
        initShortcuts();
        InventoryWidget mainWidget = new InventoryWidget(0, 0);
        selectionHandler.addWidget(mainWidget);
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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float f) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 + globalXOffset;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;

        // Pass raw mouse position to the selection handler for ViveCraft compatibility
        selectionHandler.updateMousePosition(mouseX - originX, mouseY - originY);

        for (GuiWidget widget : selectionHandler.getWidgets()) {
            widget.render(this, poseStack, originX, originY, forceAvailable);
        }
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().renderSelectedSlotName(selectionHandler.getSelectedSlot(),
                    selectionHandler.getWidgets().get(0).titleYOffset(), selectionHandler.getWidgets().get(0).getWidgetArea().getBackgroundTextureSizeX() - 40, forceAvailable);
            if (configManager.getConfig().showTooltips) {
                selectionHandler.getSelectedWidget().renderSelectedTooltip(this, poseStack, selectionHandler.getSelectedSlot(), selectionHandler.getCursorX() + originX, selectionHandler.getCursorY() + originY);
            }
        }

        if (configManager.getConfig().showCursor && !hideCursor && !ViveCraftSupport.getInstance().isActive()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            blit(poseStack, originX + (int) selectionHandler.getCursorX() - 8,
                    originY + (int) selectionHandler.getCursorY() - 8, 240, 0, 15, 15);
            poseStack.popPose();
        }
    }

    public boolean forceItemsAvailable() {
        return forceAvailable;
    }

    public void handleInput(double x, double y) {
        selectionHandler.updateSelection(x, y);
    }

    public void onSecondaryClick() {
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onSecondaryClick(this, selectionHandler.getSelectedSlot(), (int)selectionHandler.getOffsetX(), (int)selectionHandler.getOffsetY());
        }
    }

    public boolean onPrimaryClick() {
        if (selectionHandler.getSelectedSlot() != null) {
            return selectionHandler.getSelectedWidget().onPrimaryClick(this, selectionHandler.getSelectedSlot(), (int)selectionHandler.getOffsetX(), (int)selectionHandler.getOffsetY());
        }
        return true;
    }
    
}

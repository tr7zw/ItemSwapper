package dev.tr7zw.itemswapper.overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.ClientProviderManager;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.InventoryPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ItemGroupPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.ListPage;
import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.manager.itemgroups.Shortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.BackShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.ClearCurrentSlotShortcut;
import dev.tr7zw.itemswapper.manager.shortcuts.OpenInventoryShortcut;
import dev.tr7zw.itemswapper.overlay.logic.GuiSelectionHandler;
import dev.tr7zw.itemswapper.overlay.logic.GuiWidget;
import dev.tr7zw.itemswapper.overlay.logic.InventoryWidget;
import dev.tr7zw.itemswapper.overlay.logic.ListContentWidget;
import dev.tr7zw.itemswapper.overlay.logic.PaletteWidget;
import dev.tr7zw.itemswapper.overlay.logic.ShortcutListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SwitchItemOverlay extends Screen implements ItemSwapperUI {

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    public final Minecraft minecraft = Minecraft.getInstance();
    public final ClientProviderManager providerManager = ItemSwapperSharedMod.instance.getClientProviderManager();
    public final ItemSwapperClientAPI clientAPI = ItemSwapperClientAPI.getInstance();
    private final GuiSelectionHandler selectionHandler = new GuiSelectionHandler();
    public int globalXOffset = 0;
    public int globalYOffset = 0;
    public boolean forceAvailable = false;
    public boolean hideCursor = false;
    private List<Shortcut> shortcutList = Collections.emptyList();
    private List<Page> lastPages = new ArrayList<>();

    private final ConfigManager configManager = ConfigManager.getInstance();

    private SwitchItemOverlay() {
        super(Component.empty());
        super.passEvents = true;
        if (minecraft.player.isCreative() && configManager.getConfig().creativeCheatMode) {
            forceAvailable = true;
        }
    }
    
    private void initShortcuts() {
        shortcutList = Arrays.asList(ClearCurrentSlotShortcut.INSTANCE, new OpenInventoryShortcut(this), new BackShortcut(this));
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
        selectionHandler.addWidget(new ShortcutListWidget(shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
    }
    
    public void openItemList(Item[] items) {
        selectionHandler.reset();
        lastPages.add(new ListPage(items));
        initShortcuts();
        GuiWidget mainWidget = new ListContentWidget(items, 0, 0);
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
    }
    
    public void openPage(Page page) {
        if(page instanceof ItemGroupPage group) {
            openItemGroup(group.group());
        } else if(page instanceof ListPage list) {
            openItemList(list.items());
        } else if(page instanceof InventoryPage) {
            openInventory();
        }
    }
    
    public List<Page> getPageHistory(){
        return lastPages;
    }

    public void openInventory() {
        selectionHandler.reset();
        lastPages.add(new InventoryPage());
        initShortcuts();
        InventoryWidget mainWidget = new InventoryWidget(0, 0);
        selectionHandler.addWidget(mainWidget);
        selectionHandler.addWidget(new ShortcutListWidget(shortcutList,
                mainWidget.getWidgetArea().getMouseBoundsX() + ItemSwapperUI.slotSize, 0));
    }

    @Override
    public void render(PoseStack poseStack, int no1, int no2, float f) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2 + globalXOffset;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2 + globalYOffset;
        for (GuiWidget widget : selectionHandler.getWidgets()) {
            widget.render(this, poseStack, originX, originY, forceAvailable);
        }
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().renderSelectedSlotName(selectionHandler.getSelectedSlot(),
                    selectionHandler.getWidgets().get(0).titleYOffset(), forceAvailable);
        }

        if (configManager.getConfig().showCursor && !hideCursor) {
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

    public void handleSwitchSelection() {
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onClick(this, selectionHandler.getSelectedSlot());
        }
    }
    
    public void onOverlayClose() {
        if (selectionHandler.getSelectedSlot() != null) {
            selectionHandler.getSelectedWidget().onClose(this, selectionHandler.getSelectedSlot());
        }
    }

}

package dev.tr7zw.itemswapper.overlay;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.OnSwap;
import dev.tr7zw.itemswapper.api.client.ItemSwapperClientAPI.SwapSent;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class InventorySwitchItemOverlay extends SwitchItemOverlay {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inventory.png");
    private static final ItemGroup dummyItemGroup = ItemGroup.builder().withId(new ResourceLocation("itemswapper", "inventory")).withItems(new ItemEntry[0]).build();

    public InventorySwitchItemOverlay() {
        super(dummyItemGroup);
    }

    public void setupSlots() {
        setupSlots(9, 3, false, BACKGROUND_LOCATION);
//        setBackgroundTextureSizeX(168);
//        setBackgroundTextureSizeY(60);
        forceAvailable = false;
    }

    @Override
    public boolean forceItemsAvailable() {
        return false;
    }

//    @Override
//    public List<AvailableSlot> getItem(int id) {
//        NonNullList<ItemStack> items = minecraft.player.getInventory().items;
//        if (id != -1 && !items.get(id + 9).isEmpty()) {
//            return Collections.singletonList(new AvailableSlot(-1, id + 9, items.get(id + 9)));
//        }
//        return Collections.emptyList();
//    }

    /**
     * Overwrite method that only can access the inventory, no spawning items/access
     * shulkers
     */
    @Override
    public void onClose() {
//        List<AvailableSlot> slots = getItem(getSelection());
//        if (!slots.isEmpty()) {
//            AvailableSlot slot = slots.get(0);
//            if (slot.inventory() == -1) {
//                OnSwap event = clientAPI.prepareItemSwapEvent.callEvent(new OnSwap(slot, new AtomicBoolean()));
//                if(event.canceled().get()) {
//                    // interaction canceled by some other mod
//                    return;
//                }
//                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
//                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
//                        hudSlot, minecraft.player.getInventory().selected,
//                        ClickType.SWAP, this.minecraft.player);
//                clientAPI.itemSwapSentEvent.callEvent(new SwapSent(slot));
//            }
//        }
    }

    @Override
    public void handleSwitchSelection() {
//        List<AvailableSlot> slots = getItem(getSelection());
//        if (!slots.isEmpty()) {
//            AvailableSlot slot = slots.get(0);
//            if (!slot.item().isEmpty()) {
//                ItemGroup sel = ItemSwapperMod.instance.getItemGroupManager().getItemPage(slot.item().getItem());
//                if (sel != null) {
//                    ItemSwapperMod.instance.openSquareSwitchScreen(sel);
//                }
//            }
//        }
    }

}

package dev.tr7zw.itemswapper.overlay;

import java.util.Collections;
import java.util.List;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.util.ItemUtil;
import dev.tr7zw.itemswapper.util.ItemUtil.Slot;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventorySwitchItemOverlay extends SwitchItemOverlay {

    private static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("itemswapper",
            "textures/gui/inventory.png");

    public InventorySwitchItemOverlay() {
        super(new Item[0]);
    }

    public void setupSlots() {
        setupSlots(9, 3, false, BACKGROUND_LOCATION);
        setBackgroundTextureSizeX(168);
        setBackgroundTextureSizeY(60);
        forceAvailable = false;
    }
    
    @Override
    public boolean forceItemsAvailable() {
        return false;
    }

    @Override
    public List<Slot> getItem(int id) {
        NonNullList<ItemStack> items =  minecraft.player.getInventory().items;
        if(id != -1 && !items.get(id+9).isEmpty()) {
            return Collections.singletonList(new Slot(-1, id+9, items.get(id+9)));
        }
        return Collections.emptyList();
    }

    /**
     * Overwrite method that only can access the inventory, no spawning items/access shulkers
     */
    @Override
    public void onClose() {
        List<Slot> slots = getItem(getSelection());
        if (!slots.isEmpty()) {
            Slot slot = slots.get(0);
            if (slot.inventory() == -1) {
                int hudSlot = ItemUtil.inventorySlotToHudSlot(slot.slot());
                this.minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId,
                        hudSlot, minecraft.player.getInventory().selected,
                        ClickType.SWAP, this.minecraft.player);
            }
        }
    }

    @Override
    public void handleSwitchSelection() {
        List<Slot> slots = getItem(getSelection());
        if (!slots.isEmpty()) {
            Slot slot = slots.get(0);
            if(!slot.item().isEmpty()) {
                Item[] sel = ItemSwapperMod.instance.getItemGroupManager().getOpenList(slot.item().getItem());
                if(sel != null) {
                    ItemSwapperMod.instance.openSquareSwitchScreen(sel);
                }
            }
        }
    }
    
}

package dev.tr7zw.itemswapper.util;

import dev.tr7zw.transition.mc.*;
import net.minecraft.client.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;

public final class ItemUtil {

    private static final Minecraft minecraft = Minecraft.getInstance();

    private ItemUtil() {
        // private
    }

    public static int inventorySlotToHudSlot(int slot) {
        if (slot < 9) {
            return 36 + slot;
        }
        return slot;
    }

    @NotNull
    public static Item[] itemstackToSingleItem(Item[] items) {
        int lastItem = 0;
        for (int x = 0; x < items.length; x++) {
            if (items[x] != Items.AIR) {
                lastItem = x;
            }
        }
        items = Arrays.copyOf(items, lastItem + 1);
        return items;
    }

    public static void swapWithSlot(int hudSlot) {
        //? if >= 26.1 {

        minecraft.gameMode.handleContainerInput(minecraft.player.inventoryMenu.containerId, hudSlot,
                InventoryUtil.getSelectedId(minecraft.player.getInventory()),
                net.minecraft.world.inventory.ContainerInput.SWAP, minecraft.player);
        //? } else {

        /*minecraft.gameMode.handleInventoryMouseClick(minecraft.player.inventoryMenu.containerId, hudSlot,
                InventoryUtil.getSelectedId(minecraft.player.getInventory()),
                net.minecraft.world.inventory.ClickType.SWAP, minecraft.player);
        *///? }
    }

}

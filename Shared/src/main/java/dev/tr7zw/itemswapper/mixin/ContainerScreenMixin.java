package dev.tr7zw.itemswapper.mixin;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SquareSwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> {

    private final Minecraft itemswapperMinecraft = Minecraft.getInstance();
    private Item[] lastItems = null;

    public ContainerScreenMixin(ChestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        if (!ConfigManager.getInstance().getConfig().editMode) {
            return;
        }
        int limit = 25;
        Item[] items = super.getMenu().getItems().stream()
                .map(ItemStack::getItem).limit(limit).toList().toArray(new Item[0]);
        int lastItem = 0;
        for (int x = 0; x < items.length; x++) {
            if (items[x] != Items.AIR) {
                lastItem = x;
            }
        }
        items = Arrays.copyOf(items, lastItem + 1);
        SquareSwitchItemOverlay overlay = new SquareSwitchItemOverlay(ItemGroup.builder().withItems(ItemUtil.toDefault(items)).build());
        overlay.globalXOffset = -(18 * 7 + 32);
        overlay.forceAvailable = true;
        overlay.hideCursor = true;
        overlay.render(poseStack, 0, 0, f);
        if (lastItems == null) {
            lastItems = items;
        } else if (!Arrays.equals(lastItems, items)) {
            lastItems = items;
            copyString(items);
        }
    }

    private void copyString(Item[] itemArray) {
        List<String> names = Arrays.asList(itemArray).stream().map(is -> Registry.ITEM.getKey(is).toString()).toList();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ItemSwapperMod.LOGGER.info(gson.toJson(names));
        itemswapperMinecraft.keyboardHandler.setClipboard(gson.toJson(names));
    }

}

package dev.tr7zw.itemswapper.mixin;

import static dev.tr7zw.itemswapper.util.ItemUtil.itemstackToSingleItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> {

    protected ContainerScreenMixin(ChestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        if (!ConfigManager.getInstance().getConfig().editMode) {
            return;
        }
        int limit = 25;
        Item[] items = super.getMenu().getItems().stream().map(ItemStack::getItem).limit(limit).toList()
                .toArray(new Item[0]);
        items = itemstackToSingleItem(items);
        SwitchItemOverlay overlay = SwitchItemOverlay.createPaletteOverlay(
                ItemGroup.builder().withItems(ItemUtil.toDefault(items)).build());
        overlay.globalXOffset = -(18 * 7 + 32);
        overlay.forceAvailable = true;
        overlay.hideCursor = true;
        overlay.render(poseStack, 0, 0, f);
    }
}

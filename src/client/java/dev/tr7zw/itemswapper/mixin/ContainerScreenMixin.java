package dev.tr7zw.itemswapper.mixin;

import static dev.tr7zw.itemswapper.util.ItemUtil.itemstackToSingleItem;

import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.transition.config.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.manager.itemgroups.ItemGroup;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//? if >= 1.20.0 {

import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else {
/*
import com.mojang.blaze3d.vertex.PoseStack;
*///? }

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> {

    protected ContainerScreenMixin(ChestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    //? if >= 26.1 {

    @Inject(method = "extractBackground", at = @At("HEAD"))
    //? } else {

    /*@Inject(method = "render", at = @At("HEAD"))
    *///? }
       //? if >= 1.20.0 {

    public void render(GuiGraphicsExtractor graphics, int i, int j, float f, CallbackInfo ci) {
        //? } else {
        /*
            public void render(PoseStack graphics, int i, int j, float f, CallbackInfo ci) {
        *///? }
        if (!ConfigHolder.getInstance().getGeneral().getConfig().editMode) {
            return;
        }
        int limit = 25;
        Item[] items = super.getMenu().getItems().stream().map(ItemStack::getItem).limit(limit).toList()
                .toArray(new Item[0]);
        items = itemstackToSingleItem(items);
        SwitchItemOverlay overlay = SwitchItemOverlay.createInventoryOverlay();
        overlay.setGlobalXOffset(-(18 * 7 + 32));
        overlay.setForceAvailable(true);
        overlay.setHideShortcuts(true);
        overlay.setHideCursor(true);
        overlay.openItemGroup(ItemGroup.builder().withItems(items).build()); // init after setting
                                                                             // values

        //? if >= 26.1 {

        overlay.extractRenderState(graphics, 0, 0, f);
        //? } else {

        /*overlay.render(graphics, 0, 0, f);
        *///? }
    }
}

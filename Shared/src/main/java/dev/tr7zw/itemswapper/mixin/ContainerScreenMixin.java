package dev.tr7zw.itemswapper.mixin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.itemswapper.ConfigManager;
import dev.tr7zw.itemswapper.overlay.SquareSwitchItemOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> implements MenuAccess<ChestMenu> {

    private Item[] lastItems = null;
    
    public ContainerScreenMixin(ChestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        if(!ConfigManager.getInstance().getConfig().editMode) {
            return;
        }
        int limit = 20;
        try {
            limit = Integer.parseInt(title.getString());
        }catch(Exception ex) {
            
        }
        Item[] items = super.getMenu().getItems().stream()
                .map(is -> is.getItem()).limit(limit).collect(Collectors.toList()).toArray(new Item[0]);
        SquareSwitchItemOverlay overlay = new SquareSwitchItemOverlay(items, null);
        overlay.globalYOffset = - 130;
        overlay.forceAvailable = true;
        overlay.render(poseStack, 0, 0, f);
        if(lastItems == null) {
            lastItems = items;
        } else if(!Arrays.equals(lastItems, items)) {
            lastItems = items;
            copyString();
        }
    }

    private void copyString() {
        int limit = 20;
        try {
            limit = Integer.parseInt(title.getString());
        }catch(Exception ex) {
        }
        List<String> items = getMenu().getItems().stream()
                .map(is -> is.getDescriptionId()).limit(limit).collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(items));
        Minecraft.getInstance().keyboardHandler.setClipboard(gson.toJson(items));
    }

}

package dev.tr7zw.itemswapper.util;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class DummyScreen extends Screen {

    public DummyScreen() {
        super(Component.empty());
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ItemStack itemStack, int i, int j) {
        super.renderTooltip(poseStack, itemStack, i, j);
    }
    
}

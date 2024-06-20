package dev.tr7zw.itemswapper.overlay;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
// spotless:off 
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import net.minecraft.client.gui.screens.Screen;
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.client.gui.GuiComponent;
//#endif
// spotless:on
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@AllArgsConstructor
public class RenderContext {

    @SuppressWarnings("unused")
    private final static Minecraft minecraft = Minecraft.getInstance();

    // spotless:off 
    //#if MC >= 12000
    private final GuiGraphics guiGraphics;
    //#else
    //$$ private final Screen screen;
    //$$ private final PoseStack pose;
    //#endif
    // spotless:on

    public PoseStack pose() {
        // spotless:off 
        //#if MC >= 12000
        return guiGraphics.pose();
        //#else
        //$$ return pose;
        //#endif
        // spotless:on
    }

    // spotless:off
    //#if MC >= 12100
    public MultiBufferSource.BufferSource getbufferSource()
    {
        return guiGraphics.bufferSource();
    }
    //#endif
    // spotless:on

    public void blit(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height,
            int textureWidth, int textureHeight) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.blit(atlasLocation, x, y, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //$$ RenderSystem.setShaderTexture(0, atlasLocation);
        //$$ screen.blit(pose, x, y, y, uOffset, vOffset, width, height, textureWidth, textureHeight);
        //#endif
        // spotless:on
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.blit(atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //$$ RenderSystem.setShaderTexture(0, atlasLocation);
        //$$ screen.blit(pose, x, y, uOffset, vOffset, uWidth, vHeight);
        //#endif
        // spotless:on
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, int blitOffset, float uOffset, float vOffset,
            int uWidth, int vHeight, int textureWidth, int textureHeight) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.blit(atlasLocation, x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureWidth,
                textureHeight);
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //$$ RenderSystem.setShaderTexture(0, atlasLocation);
        //$$ GuiComponent.blit(pose, x, y, blitOffset, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight);
        //#endif
        // spotless:on
    }

    public void blitSprite(ResourceLocation hotbarOffhandLeftSprite, int x, int y, int width, int height) {
        // spotless:off 
        //#if MC >= 12002
        guiGraphics.blitSprite(hotbarOffhandLeftSprite, x, y, width, height);
        //#else
        //$$ throw new java.lang.RuntimeException();
        //#endif
        // spotless:on
    }

    public void renderTooltip(Font font, List<FormattedCharSequence> split, int x, int y) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.renderTooltip(font, split, x, y);
        //#else
        //$$ screen.renderTooltip(pose, split, x, y);
        //#endif
        // spotless:on
    }

    public void renderTooltip(Font font, MutableComponent translatable, int x, int y) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.renderTooltip(font, translatable, x, y);
        //#else
        //$$ screen.renderTooltip(pose, translatable, x, y);
        //#endif
        // spotless:on
    }

    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.fill(minX, minY, maxX, maxY, color);
        //#else
        //$$ GuiComponent.fill(pose, minX, minY, maxX, maxY, color);
        //#endif
        // spotless:on
    }

    public void renderFakeItem(ItemStack itemStack, int x, int y) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.renderFakeItem(itemStack, x, y);
        //#else
        //$$ minecraft.getItemRenderer().renderAndDecorateFakeItem(pose, itemStack, x, y);
        //#endif
        // spotless:on
    }

    public void renderItemDecorations(Font font, ItemStack itemStack, int x, int y) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.renderItemDecorations(font, itemStack, x, y);
        //#else
        //$$ minecraft.getItemRenderer().renderGuiItemDecorations(pose, font, itemStack, x, y);
        //#endif
        // spotless:on
    }

    public void renderItem(Player player, ItemStack itemStack, int x, int y, int seed) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.renderItem(player, itemStack, x, y, seed);
        //#else
        //$$ minecraft.getItemRenderer().renderAndDecorateItem(pose, player, itemStack, x, y, seed);
        //#endif
        // spotless:on
    }

    public void drawString(Font font, Component name, int x, int y, int color) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.drawString(font, name, x, y, color);
        //#else
        //$$ screen.drawString(pose, font, name, x, y, color);
        //#endif
        // spotless:on
    }

    public void drawCenteredString(Font font, Component name, int x, int y, int color) {
        // spotless:off 
        //#if MC >= 12000
        guiGraphics.drawCenteredString(font, name, x, y, color);
        //#else
        //$$ screen.drawCenteredString(pose, font, name, x, y, color);
        //#endif
        // spotless:on
    }

}

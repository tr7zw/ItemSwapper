package dev.tr7zw.itemswapper.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RenderHelper {

    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    
    private float blitOffset;
    
    public void renderGrayedOutItem(LivingEntity livingEntity, ItemStack itemStack, int i, int j, int k) {
        if (itemStack.isEmpty())
            return;
        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, livingEntity, k);
        this.blitOffset = bakedModel.isGui3d() ? (this.blitOffset + 50.0F) : (this.blitOffset + 50.0F);
        try {
            renderGuiItem(itemStack, i, j, bakedModel);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
            crashReportCategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashReportCategory.setDetail("Item Damage", () -> String.valueOf(itemStack.getDamageValue()));
            crashReportCategory.setDetail("Item NBT", () -> String.valueOf(itemStack.getTag()));
            crashReportCategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
            throw new ReportedException(crashReport);
        }
        this.blitOffset = bakedModel.isGui3d() ? (this.blitOffset - 50.0F) : (this.blitOffset - 50.0F);
    }
    
    @SuppressWarnings("deprecation")
    protected void renderGuiItem(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(0F, 0F, 0F, 0.5F);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(i, j, (100.0F + this.blitOffset));
        poseStack.translate(8.0D, 8.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = !bakedModel.usesBlockLight();
        if (bl)
            Lighting.setupForFlatItems();
        itemRenderer.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack2, bufferSource, 0,
                OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (bl)
            Lighting.setupFor3DItems();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
    
}

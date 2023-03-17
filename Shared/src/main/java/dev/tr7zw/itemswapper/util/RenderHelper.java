package dev.tr7zw.itemswapper.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.LinkIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class RenderHelper {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static float blitOffset;

    private RenderHelper() {
        // private
    }

    public static void renderUnavailableItem(PoseStack poseStack, LivingEntity livingEntity, ItemStack itemStack, int i,
            int j, int k, SlotEffect effect) {
        if (itemStack.isEmpty())
            return;
        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, livingEntity, k);
        blitOffset = bakedModel.isGui3d() ? (blitOffset + 50.0F) : (blitOffset + 50.0F);
        int l = i;
        int m = j;
        int color = 0;
        if(effect == SlotEffect.RED) {
            color = 822018048;
        } else if(effect == SlotEffect.GRAY) {
            color = -1879048192;
        }
        // these values need to be fixed when the texture size gets fixed.
        GuiComponent.fill(poseStack, l - 1, m - 1, l + 17, m + 17, color);
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        itemRenderer.renderAndDecorateFakeItem(itemStack, l, m);
        if (k == 0)
            itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, l, m);
        blitOffset = bakedModel.isGui3d() ? (blitOffset - 50.0F) : (blitOffset - 50.0F);
    }

    public static void renderGuiItemCount(Font font, String text, int i, int j, int color) {
        renderGuiItemText(font, text, (i + 19 - 2 - font.width(text)), (j + 6 + 3), color);
    }

    public static void renderGuiItemName(Font font, String text, int i, int j, int color) {
        renderGuiItemText(font, text, (i - font.width(text) / 2), j, color);
    }

    public static void renderGuiItemName(Font font, List<FormattedCharSequence> text, int x, int y, int color) {
        renderGuiItemText(font, text, x, y, color);
    }
 
    public static void renderGuiItemText(Font font, List<FormattedCharSequence> text, int x, int y, int color) {
        PoseStack poseStack = new PoseStack();
        for(int line = 0; line < text.size(); line++) {
            poseStack.translate(0.0D, 0.0D, (minecraft.getItemRenderer().blitOffset + 200.0F));
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                    .immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text.get(line), (x - font.width(text.get(line)) / 2), y - (font.lineHeight * (text.size() - line)), color, true,
                    poseStack.last().pose(), bufferSource, false, 0, 15728880);
            bufferSource.endBatch();
        }
    }
    
    public static void renderGuiItemText(Font font, String text, int i, int j, int color) {
        PoseStack poseStack = new PoseStack();
        String string2 = text;
        poseStack.translate(0.0D, 0.0D, (minecraft.getItemRenderer().blitOffset + 200.0F));
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                .immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(string2, i, j, color, true,
                poseStack.last().pose(), bufferSource, false, 0, 15728880);
        bufferSource.endBatch();
    }
    
    public enum SlotEffect {
        NONE, RED, GRAY
    }

    public static void renderSlot(PoseStack poseStack, int x, int y, Player arg, ItemStack arg2, int k, SlotEffect effect,
            int count) {
        if (!arg2.isEmpty()) {
            ItemStack copy = arg2.copy();
            copy.setCount(1);
            if (effect != SlotEffect.NONE) {
                RenderHelper.renderUnavailableItem(poseStack, arg, copy, x, y, k, effect);
                return;
            }
            minecraft.getItemRenderer().renderAndDecorateItem(arg, copy, x, y, k);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            minecraft.getItemRenderer().renderGuiItemDecorations(minecraft.font, copy, x, y);
            int color = count > 64 ? 0xFFFF00 : 0xFFFFFF;
            if (count > 1)
                RenderHelper.renderGuiItemCount(minecraft.font, "" + Math.min(64, count), x, y, color);
        }
    }

    public static Component getName(ItemEntry entry) {
        if (entry == null) {
            return null;
        }
        if (entry.getNameOverwride() != null) {
            return entry.getNameOverwride();
        }
        return entry.getItem().getDefaultInstance().getHoverName();
    }
    
    public static Component getName(ItemIcon entry) {
        if (entry == null) {
            return null;
        }
        if (entry.nameOverwrite() != null) {
            return entry.nameOverwrite();
        }
        return entry.item().getHoverName();
    }
    
    public static Component getName(LinkIcon entry) {
        if (entry == null) {
            return null;
        }
        if (entry.nameOverwrite() != null) {
            return entry.nameOverwrite();
        }
        return entry.item().getHoverName();
    }


    public static void renderSelectedItemName(Component comp, ItemStack arg2, boolean grayOut, int offsetY, int maxWidth) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        TextColor textColor = arg2.getHoverName().getStyle().getColor();
        ChatFormatting rarityColor = arg2.getRarity().color;
        int color = 0xFFFFFF;
        if (grayOut) {
            color = 0xAAAAAA;
        } else if (textColor != null) {
            color = textColor.getValue();
        } else if (rarityColor != null && rarityColor.getColor() != null) {
            color = rarityColor.getColor();
        }
        RenderHelper.renderGuiItemName(minecraft.font, minecraft.font.split(comp, maxWidth), originX, originY - (offsetY / 2) - 12, color);
    }
    
    public static void renderSelectedEntryName(Component comp, boolean grayOut, int offsetY, int maxWidth) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        int color = 0xFFFFFF;
        if (grayOut) {
            color = 0xAAAAAA;
        }
        RenderHelper.renderGuiItemName(minecraft.font, minecraft.font.split(comp, maxWidth), originX, originY - (offsetY / 2) - 12, color);
    }

}

package dev.tr7zw.itemswapper.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import dev.tr7zw.itemswapper.manager.itemgroups.Icon.ItemIcon;
import dev.tr7zw.itemswapper.manager.itemgroups.Icon.LinkIcon;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class RenderHelper {

    private static final Minecraft minecraft = Minecraft.getInstance();

    private RenderHelper() {
        // private
    }

    public static void renderUnavailableItem(RenderContext graphics, LivingEntity livingEntity, ItemStack itemStack,
            int i, int j, int k, SlotEffect effect) {
        if (itemStack.isEmpty())
            return;
        float blitOffset = 0;
        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, livingEntity, k);
        blitOffset = bakedModel.isGui3d() ? (blitOffset + 50.0F) : (blitOffset + 50.0F);
        int l = i;
        int m = j;
        int color = 0;
        if (effect == SlotEffect.RED) {
            color = 822018048;
        } else if (effect == SlotEffect.GRAY) {
            color = -1879048192;
        }
        // these values need to be fixed when the texture size gets fixed.
        graphics.fill(l - 1, m - 1, l + 17, m + 17, color);
        graphics.renderFakeItem(itemStack, l, m);
        if (k == 0)
            graphics.renderItemDecorations(minecraft.font, itemStack, l, m);
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
        for (int line = 0; line < text.size(); line++) {
            poseStack.translate(0.0D, 0.0D, (400.0F));
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                    .immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text.get(line), (x - font.width(text.get(line)) / 2),
                    y - (font.lineHeight * (text.size() - line)), color, true, poseStack.last().pose(), bufferSource,
                    Font.DisplayMode.NORMAL, 0, 15728880);
            bufferSource.endBatch();
        }
    }

    public static void renderGuiItemText(Font font, String text, int i, int j, int color) {
        PoseStack poseStack = new PoseStack();
        String string2 = text;
        poseStack.translate(0.0D, 0.0D, 400.0F);
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                .immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(string2, (float) i, (float) j, color, true, poseStack.last().pose(), bufferSource,
                Font.DisplayMode.NORMAL, 0, 15728880);
        bufferSource.endBatch();
    }

    public enum SlotEffect {
        NONE, RED, GRAY
    }

    public static void renderSlot(RenderContext graphics, int x, int y, Player arg, ItemStack arg2, int k,
            SlotEffect effect, int count) {
        if (!arg2.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200);
            ItemStack copy = arg2.copy();
            copy.setCount(1);
            if (effect != SlotEffect.NONE) {
                RenderHelper.renderUnavailableItem(graphics, arg, copy, x, y, k, effect);
                graphics.pose().popPose();
                return;
            }
            graphics.renderItem(arg, copy, x, y, k);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            graphics.renderItemDecorations(minecraft.font, copy, x, y);
            int color = count > 64 ? 0xFFFF00 : 0xFFFFFF;
            if (count > 1)
                RenderHelper.renderGuiItemCount(minecraft.font, "" + Math.min(64, count), x, y, color);
            graphics.pose().popPose();
        }
    }

    public static Component getName(ItemEntry entry) {
        if (entry == null) {
            return null;
        }
        if (entry.getNameOverwride() != null) {
            return entry.getNameOverwride();
        }
        return ItemUtil.getDisplayname(entry.getItem().getDefaultInstance());
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

    public static void renderSelectedItemName(Component comp, ItemStack arg2, boolean grayOut, int offsetY,
            int maxWidth) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        TextColor textColor = arg2.getHoverName().getStyle().getColor();
        // spotless:off 
        //#if MC <= 12004
        //$$ ChatFormatting rarityColor = arg2.getRarity().color;
        //#else
        ChatFormatting rarityColor = arg2.getRarity().color();
        //#endif
        //spotless:on
        int color = 0xFFFFFF;
        if (grayOut) {
            color = 0xAAAAAA;
        } else if (textColor != null) {
            color = textColor.getValue();
        } else if (rarityColor != null && rarityColor.getColor() != null) {
            color = rarityColor.getColor();
        }
        RenderHelper.renderGuiItemName(minecraft.font, minecraft.font.split(comp, maxWidth), originX,
                originY - (offsetY / 2) - 12, color);
    }

    public static void renderSelectedEntryName(Component comp, boolean grayOut, int offsetY, int maxWidth) {
        int originX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int originY = minecraft.getWindow().getGuiScaledHeight() / 2;
        int color = 0xFFFFFF;
        if (grayOut) {
            color = 0xAAAAAA;
        }
        RenderHelper.renderGuiItemName(minecraft.font, minecraft.font.split(comp, maxWidth), originX,
                originY - (offsetY / 2) - 12, color);
    }

}

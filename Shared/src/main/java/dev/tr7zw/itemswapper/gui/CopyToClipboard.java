package dev.tr7zw.itemswapper.gui;

import static dev.tr7zw.itemswapper.util.ItemUtil.itemstackToSingleItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CopyToClipboard extends ImageButton {
    private static final ResourceLocation texture = new ResourceLocation(ItemSwapperSharedMod.MODID, "textures/gui/button.png");
    private static final int TEXTURE_WIDTH = 20;
    private static final int TEXTURE_HEIGHT = 37;
    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 18;

    private final Minecraft instance = Minecraft.getInstance();
    private Item[] lastItems = null;


    public CopyToClipboard(int i, int j) {
        super(i, j, 10, 9, 0, 0, 19, texture, TEXTURE_WIDTH, TEXTURE_HEIGHT, null, Component.literal(""));
    }

    @Override
    public void onPress() {
        if (instance.player == null) {
            return;
        }

        int limit = 25;
        Item[] items = instance.player.containerMenu.getItems().stream().map(ItemStack::getItem)
                .limit(limit).toList().toArray(new Item[0]);

        items = itemstackToSingleItem(items);
        if (lastItems == null || !Arrays.equals(lastItems, items)) {
            lastItems = items;
        }

        String json = arrayToJson(items);

        Minecraft.getInstance().keyboardHandler.setClipboard(json);
        ItemSwapperMod.LOGGER.info(json);
        instance.player.sendSystemMessage(Component.translatable("text.itemswapper.button.copyToClipboard.success"));

    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        poseStack.scale(.5f, .5f, 1);
        poseStack.translate(x, y, 0);
        blit(poseStack, this.x, this.y, 0, this.isHovered ? 19 : 0, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH,
                TEXTURE_HEIGHT);
        this.renderToolTip(poseStack, i, j);
        poseStack.popPose();
    }

    @Override
    public void renderToolTip(@NotNull PoseStack poseStack, int i, int j) {
        if (this.isHovered && instance.screen != null) {
            instance.screen.renderTooltip(poseStack,
                    Component.translatable("text.itemswapper.button.copyToClipboard.tooltip"), i, j);
        }
    }

    private String arrayToJson(Item[] itemArray) {
        List<String> names = Arrays.stream(itemArray).map(is -> Registry.ITEM.getKey(is).toString()).toList();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(names);
    }
}

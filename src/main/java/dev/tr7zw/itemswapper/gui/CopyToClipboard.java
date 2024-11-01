package dev.tr7zw.itemswapper.gui;

import static dev.tr7zw.itemswapper.util.ItemUtil.itemstackToSingleItem;
import static dev.tr7zw.util.NMSHelper.getResourceLocation;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.overlay.RenderContext;
import dev.tr7zw.itemswapper.ItemSwapperBase;
import dev.tr7zw.util.ComponentProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//spotless:off 
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on
import net.minecraft.client.gui.screens.Screen;

// spotless:off 
//#if MC >= 12002
import com.terraformersmc.modmenu.gui.widget.LegacyTexturedButtonWidget;

public class CopyToClipboard extends LegacyTexturedButtonWidget {
//#else
//$$ import net.minecraft.client.gui.components.ImageButton;
//$$
//$$ public class CopyToClipboard extends ImageButton {    
//#endif
//spotless:on

    private static final ResourceLocation texture = getResourceLocation(ItemSwapperBase.MODID,
            "textures/gui/button.png");

    private static final int TEXTURE_WIDTH = 10;
    private static final int TEXTURE_HEIGHT = 18;
    private static final int BUTTON_WIDTH = 10;
    private static final int BUTTON_HEIGHT = 9;

    private final Minecraft instance = Minecraft.getInstance();
    private Item[] lastItems = null;
    private final Screen screen;

    public CopyToClipboard(Screen screen, int i, int j) {
        super(i, j, 10, 9, 0, 0, 19, texture, TEXTURE_WIDTH, TEXTURE_HEIGHT, null, CommonComponents.EMPTY);
        this.screen = screen;
    }

    @Override
    public void onPress() {
        if (instance.player == null) {
            return;
        }

        int limit = 25;
        Item[] items = instance.player.containerMenu.getItems().stream().map(ItemStack::getItem).limit(limit).toList()
                .toArray(new Item[0]);

        items = itemstackToSingleItem(items);
        if (lastItems == null || !Arrays.equals(lastItems, items)) {
            lastItems = items;
        }

        String json = arrayToJson(items);

        Minecraft.getInstance().keyboardHandler.setClipboard(json);
        ItemSwapperMod.LOGGER.info(json);
//        instance.player
//                .sendSystemMessage(ComponentProvider.translatable("text.itemswapper.button.copyToClipboard.success"));

    }

    @Override
    // spotless:off 
  //#if MC >= 12000
    public void renderWidget(@NotNull GuiGraphics graphics, int i, int j, float f) {
        RenderContext renderContext = new RenderContext(graphics);
    //#else
    //$$ public void renderWidget(@NotNull PoseStack pose, int i, int j, float f) {
    //$$ RenderContext renderContext = new RenderContext(screen, pose);
    //#endif
    // spotless:on
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        // FIXME: Cursed and broken, but doesn't scale everything anymore
        renderContext.blit(texture, this.getX(), this.getY(), 0, this.isHovered ? 9 : 0, BUTTON_WIDTH, BUTTON_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
        this.renderToolTip(renderContext, i, j);
    }

    public void renderToolTip(@NotNull RenderContext renderContext, int i, int j) {
        if (this.isHovered && instance.screen != null) {
            renderContext.renderTooltip(Minecraft.getInstance().font,
                    ComponentProvider.translatable("text.itemswapper.button.copyToClipboard.tooltip"), i, j);
        }
    }

    private String arrayToJson(Item[] itemArray) {
        List<String> names = Arrays.stream(itemArray).map(is -> BuiltInRegistries.ITEM.getKey(is).toString()).toList();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(names);
    }
}

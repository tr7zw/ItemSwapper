package dev.tr7zw.itemswapper.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.gui.CopyToClipboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

//spotless:off 
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Shadow
    protected int imageWidth;
    @Shadow
    protected int imageHeight;
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;

    private CopyToClipboard copyToClipboardBtn;

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (!ConfigManager.getInstance().getConfig().editMode || minecraft == null || minecraft.player == null) {
            return;
        }

        AbstractContainerMenu currentMenu = Objects.requireNonNull(Minecraft.getInstance().player).containerMenu;

        if (currentMenu instanceof ChestMenu cm) {
            copyToClipboardBtn = new CopyToClipboard(this, this.leftPos + this.imageWidth - 20, this.topPos + 5);
            this.addRenderableWidget(copyToClipboardBtn);
            ItemSwapperMod.LOGGER.debug("Copy to Clipboard button created");
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    // spotless:off 
  //#if MC >= 12000
    private void render(GuiGraphics graphics, int i, int j, float f, CallbackInfo info) {
        //#else
        //$$ private void render(PoseStack graphics, int i, int j, float f, CallbackInfo info) {
        //#endif
        // spotless:on
        if (copyToClipboardBtn != null) {
            copyToClipboardBtn.setX(this.leftPos + this.imageWidth - 20);
        }
    }
}

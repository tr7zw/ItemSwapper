package dev.tr7zw.itemswapper.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.itemswapper.gui.CopyToClipboard;
import dev.tr7zw.itemswapper.ItemSwapperMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (currentMenu instanceof ChestMenu) {
            copyToClipboardBtn = new CopyToClipboard(this.leftPos + this.imageWidth - 20,
                    this.topPos + this.imageHeight - 163);
            this.addRenderableWidget(copyToClipboardBtn);
            ItemSwapperMod.LOGGER.debug("Copy to Clipboard button created");
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(PoseStack poseStack, int i, int j, float f, CallbackInfo info) {
        if (copyToClipboardBtn != null) {
            copyToClipboardBtn.x = this.leftPos + this.imageWidth - 20;
        }
    }
}

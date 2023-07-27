package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import dev.tr7zw.itemswapper.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult.Type;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    private Screen screen;
    @Shadow
    private LocalPlayer player;

    @Redirect(method = "runTick", at = @At(target = "Lnet/minecraft/client/server/IntegratedServer;isPublished()Z", value = "INVOKE", ordinal = 0))
    private boolean dontPauseSingleplayer(IntegratedServer server, boolean bl) {
        if (Minecraft.getInstance().getOverlay() instanceof ItemSwapperUI) {
            return true;
        }
        if (screen instanceof ItemSwapperUI) {
            return true;
        }
        return server.isPublished();
    }

    // FIXME
    @Inject(method = "createSearchTrees", at = @At("HEAD"))
    private void createSearchTrees(CallbackInfo ci) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener(new SwapperResourceLoader());
    }

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void pickBlock(CallbackInfo ci) {
        if (screen instanceof ItemSwapperUI) {
            ci.cancel();
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void startUseItem(CallbackInfo ci) {
        if (screen instanceof ItemSwapperUI) {
            ci.cancel();
        }
    }

    @Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void pickBlockShulkerSupport(CallbackInfo ci, boolean creative, BlockEntity blockEntity, ItemStack stack, Type type) {
        if(creative) {
            return;
        }
        int slotId = player.getInventory().findSlotMatchingItem(stack);
        if (slotId != -1) {
            return;
        }
        ItemUtil.grabItem(stack.getItem(), false);
        ci.cancel();
    }

}

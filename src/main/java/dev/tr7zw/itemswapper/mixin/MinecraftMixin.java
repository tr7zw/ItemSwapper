package dev.tr7zw.itemswapper.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static dev.tr7zw.util.NMSHelper.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.ItemSwapperUI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.config.PickBlockMode;
import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;
import dev.tr7zw.itemswapper.util.ItemUtil;

import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult.Type;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    public Screen screen;
    @Shadow
    public LocalPlayer player;

    @Shadow
    public ClientLevel level;

    @Shadow
    public HitResult hitResult;

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

    //#if MC >= 12104
    @Unique
    private ItemStack getHitResultStack(HitResult hitResult, boolean ctrl) {
        if (hitResult == null)
            return ItemStack.EMPTY;
        return switch (hitResult.getType()) {
        case BLOCK -> {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.isAir()) {
                yield ItemStack.EMPTY;
            }
            ItemStack itemStack = blockState.getCloneItemStack(this.level, pos, ctrl);
            if (itemStack.isEmpty()) {
                yield ItemStack.EMPTY;
            }
            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            if (blockEntity != null) {
                CompoundTag compoundTag = blockEntity.saveCustomOnly(level.registryAccess());
                blockEntity.removeComponentsFromTag(compoundTag); // Deprecated might go bye bye soon
                BlockItem.setBlockEntityData(itemStack, blockEntity.getType(), compoundTag);
                itemStack.applyComponents(blockEntity.collectComponents());
            }
            yield itemStack;
        }
        case ENTITY -> {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            ItemStack itemStack = entity.getPickResult();
            yield itemStack == null ? ItemStack.EMPTY : itemStack;
        }
        default -> ItemStack.EMPTY;
        };
    }

    @Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;hasControlDown()Z", shift = At.Shift.AFTER), cancellable = true)
    private void pickBlockShulkerSupport(CallbackInfo ci) {
        boolean creative = player.getAbilities().instabuild;
        ItemStack stack = getHitResultStack(this.hitResult, Screen.hasControlDown());
        //#else
        //$$@Inject(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
        //$$private void pickBlockShulkerSupport(CallbackInfo ci, boolean creative, BlockEntity blockEntity, ItemStack stack,
        //$$        Type type) {
        //#endif
        if (creative) {
            return;
        }
        if (ConfigManager.getInstance().getConfig().pickblockOnToolsWeapons != PickBlockMode.ALLOW) {
            ItemList list = ItemSwapperSharedMod.instance.getItemGroupManager()
                    .getList(player.getMainHandItem().getItem());

            if (list != null && (list.getId().equals(getResourceLocation("itemswapper", "v2/weapons"))
                    || list.getId().equals(getResourceLocation("itemswapper", "v2/tools")))) {
                if (ConfigManager.getInstance().getConfig().pickblockOnToolsWeapons == PickBlockMode.PREVENT_ON_TOOL) {
                    // skip vanilla logic
                    ci.cancel();
                }
                // else it's VANILLA_ON_TOOL, so just do that
                return;
            }
        }
        int slotId = player.getInventory().findSlotMatchingItem(stack);
        if (slotId != -1) {
            return;
        }
        ItemUtil.grabItem(stack.getItem(), false);
        ci.cancel();
    }

}

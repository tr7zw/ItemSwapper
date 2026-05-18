package dev.tr7zw.itemswapper.mixin;

import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.transition.config.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;
import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.manager.itemgroups.ItemList;

import fi.dy.masa.litematica.materials.MaterialCache;
import fi.dy.masa.litematica.util.RayTraceUtils;
import fi.dy.masa.litematica.util.WorldUtils;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(WorldUtils.class)
public class LitematicaMixin {

    @Inject(method = "doSchematicWorldPickBlock", at = @At("HEAD"), remap = true, cancellable = true)
    private static void doSchematicWorldPickBlockHook(boolean closest, Minecraft mc,
            CallbackInfoReturnable<Boolean> ci) {
        if (ConfigHolder.getInstance().getGeneral().getConfig().pickblockOnToolsWeapons != PickBlockMode.ALLOW) {
            ItemList list = ItemSwapperSharedMod.instance.getItemGroupManager()
                    .getList(mc.player.getMainHandItem().getItem());

            if (list != null && (list.getId().equals(getResourceLocation("itemswapper", "v2/weapons"))
                    || list.getId().equals(getResourceLocation("itemswapper", "v2/tools")))) {

                if (ConfigHolder.getInstance().getGeneral()
                        .getConfig().pickblockOnToolsWeapons == PickBlockMode.PREVENT_ON_TOOL) {
                    // skip Litematica logic
                    ci.setReturnValue(true);
                    ci.cancel();
                }
                // else it's VANILLA_ON_TOOL, so just do that
                return;
            }
        }
        // pickblock from shulker

        BlockPos pos;
        pos = RayTraceUtils.getSchematicWorldTraceIfClosest(mc.level, mc.player, 6);
        if (pos != null) {
            Level world = SchematicWorldHandler.getSchematicWorld();
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(state, world, pos);
                ItemSwapperSharedMod.instance.getItemManager().grabItem(stack.getItem(), false);
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

}

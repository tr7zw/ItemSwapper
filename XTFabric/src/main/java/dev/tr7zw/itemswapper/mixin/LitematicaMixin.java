package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr7zw.itemswapper.util.ItemUtil;
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
    private static void doSchematicWorldPickBlockHook(boolean closest, Minecraft mc, CallbackInfoReturnable<Boolean> ci) {
        BlockPos pos;

        pos = RayTraceUtils.getSchematicWorldTraceIfClosest(mc.level, mc.player, 6);
        if (pos != null) {
            Level world = SchematicWorldHandler.getSchematicWorld();
            if(world != null) {
                BlockState state = world.getBlockState(pos);
                ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(state, world, pos);
                ItemUtil.grabItem(stack.getItem(), false);
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }
}

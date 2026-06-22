//? if >= 1.21 {
package dev.tr7zw.itemswapper.compat;

import dev.tr7zw.itemswapper.*;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.manager.itemgroups.*;
import dev.tr7zw.transition.mc.*;
import fi.dy.masa.litematica.interfaces.*;
import fi.dy.masa.litematica.materials.*;
import fi.dy.masa.litematica.schematic.pickblock.*;
import fi.dy.masa.litematica.util.*;
import fi.dy.masa.litematica.world.*;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.*;

import java.lang.reflect.*;
import java.util.function.*;

import static dev.tr7zw.transition.mc.GeneralUtil.getResourceLocation;

/*
* Add support for Litematica's schematic pick block events.
* Lightly based on https://github.com/Moncef-dev/builders-shulkers/blob/main/src/client/java/io/github/moncefdev/shulkerinventory/client/compat/LitematicaPickBlockCompat.java MIT Copyright (c) 2026 Moncef-dev
 */
public class LitematicaSupport implements ISchematicPickBlockEventListener {

    private static final String LISTENER_NAME = "ItemSwapperLitematicaSupport";
    private static Field processingCancelledField;

    public static void init() {
        SchematicPickBlockEventHandler.getInstance().registerSchematicPickBlockEventListener(new LitematicaSupport());
    }

    @Override
    public Supplier<String> getName() {
        return () -> LISTENER_NAME;
    }

    @Override
    public void onSchematicPickBlockCancelled(Supplier<String> supplier) {
        if (supplier != null && LISTENER_NAME.equals(supplier.get())) {
            resetProcessingCancelled();
        }
    }

    @Override
    public SchematicPickBlockEventResult onSchematicPickBlockStart(boolean b) {
        return SchematicPickBlockEventResult.SUCCESS;
    }

    @Override
    public SchematicPickBlockEventResult onSchematicPickBlockPreGather(Level level, BlockPos blockPos,
            BlockState blockState) {
        return SchematicPickBlockEventResult.SUCCESS;
    }

    @Override
    public SchematicPickBlockEventResult onSchematicPickBlockPrePick(Level level, BlockPos blockPos,
            BlockState blockState, ItemStack itemStack) {
        if (ConfigHolder.getInstance().getGeneral().getConfig().pickblockOnToolsWeapons != PickBlockMode.ALLOW) {
            ItemList list = ItemSwapperSharedMod.instance.getItemGroupManager()
                    .getList(GeneralUtil.getPlayer().getMainHandItem().getItem());

            if (list != null && (list.getId().equals(getResourceLocation("itemswapper", "v2/weapons"))
                    || list.getId().equals(getResourceLocation("itemswapper", "v2/tools")))) {

                if (ConfigHolder.getInstance().getGeneral()
                        .getConfig().pickblockOnToolsWeapons == PickBlockMode.PREVENT_ON_TOOL) {
                    // skip Litematica logic
                    return SchematicPickBlockEventResult.CANCEL;
                }
                // else it's VANILLA_ON_TOOL, so just do that
                return SchematicPickBlockEventResult.SUCCESS;
            }
        }
        // pickblock from shulker

        BlockPos pos;
        pos = RayTraceUtils.getSchematicWorldTraceIfClosest(GeneralUtil.getWorld(), GeneralUtil.getPlayer(), 6);
        if (pos != null) {
            Level world = SchematicWorldHandler.getSchematicWorld();
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                ItemStack stack = MaterialCache.getInstance().getRequiredBuildItemForState(state, world, pos);
                ItemSwapperSharedMod.instance.getItemManager().grabLocalItem(stack.getItem(), false);
                return SchematicPickBlockEventResult.CANCEL;
            }
        }
        return SchematicPickBlockEventResult.SUCCESS;
    }

    @Override
    public void onSchematicPickBlockSuccess() {
        // Nothing to do here
    }

    // Reflection workaround for the upstream sticky-flag bug: set SchematicPickBlockEventHandler.processingCancelled back
    // to false (there is no public reset). Remove once Litematica resets the flag itself.
    private static void resetProcessingCancelled() {
        try {
            if (processingCancelledField == null) {
                processingCancelledField = SchematicPickBlockEventHandler.class.getDeclaredField("processingCancelled");
                processingCancelledField.setAccessible(true);
            }
            processingCancelledField.setBoolean(SchematicPickBlockEventHandler.getInstance(), false);
        } catch (Throwable t) {
            ItemSwapperBase.LOGGER.error(
                    "Failed to reset Litematica pick block processingCancelled flag! This may cause issues with future pick block attempts in Litematica until you reload the world.",
                    t);
        }
    }
}
//? }
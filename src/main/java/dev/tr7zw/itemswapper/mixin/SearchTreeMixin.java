package dev.tr7zw.itemswapper.mixin;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class SearchTreeMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    private void registerReloadListenerMixin(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager,
            ProfilerFiller profilerFiller, CallbackInfo ci) {
        SwapperResourceLoader.load(map, resourceManager, profilerFiller);
        LogUtils.getLogger().info("Loaded swapper packs");
    }
}
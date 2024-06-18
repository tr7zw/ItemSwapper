package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import dev.tr7zw.itemswapper.manager.SwapperResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.ResourceLocationSearchTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(ResourceLocationSearchTree.class)
public class SearchTreeMixin {
    @Inject(method = "create", at = @At("HEAD"))
    private static <T> void create(List<T> contents, Function<T, Stream<ResourceLocation>> idGetter,
            CallbackInfoReturnable<ResourceLocationSearchTree<T>> cir) {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener(new SwapperResourceLoader());
    }
}

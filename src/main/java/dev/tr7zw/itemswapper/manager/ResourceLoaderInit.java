package dev.tr7zw.itemswapper.manager;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class ResourceLoaderInit {

    public static void init() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener) new SwapperResourceLoader());
    }
}

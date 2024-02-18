package dev.tr7zw.itemswapper;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("itemswapper")
public class ItemSwapperMod extends ItemSwapperSharedMod {

    public ItemSwapperMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        } catch (Throwable ex) {
            LOGGER.warn("ItemSwapper Mod installed on a Server. Going to sleep.");
            return;
        }
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
        init();
    }

    @Override
    public void initModloader() {
        // TODO Auto-generated method stub

    }

}

package dev.tr7zw.itemswapper.util;

import dev.tr7zw.transition.mc.*;
import net.minecraft.server.*;
import net.minecraft.world.level.*;

import java.lang.ref.*;

public class LevelProvider {

    private static WeakReference<MinecraftServer> server = new WeakReference<>(null);

    public static Level getLevel() {
        var l = server.get();
        if (l == null) {
            return GeneralUtil.getWorld();
        }
        return l.findRespawnDimension();
    }

    public static void setServer(MinecraftServer minecraftServer) {
        server = new WeakReference<>(minecraftServer);
    }

}

package dev.tr7zw.itemswapper.server.manger;

import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.packets.clientbound.*;
import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.transition.loader.networking.*;
import net.minecraft.server.level.*;

import java.util.*;

public class ServerPlayerManager {

    private final Map<ServerPlayer, PlayerSession> playerSessions = new HashMap<>();

    public void onJoin(ServerPlayer serverPlayer) {
        PlayerSession session = new PlayerSession();
        playerSessions.put(serverPlayer, session);
        if (ConfigHolder.getInstance().getGeneral().getConfig().serverPreventModUsage) {
            ServerNetworkUtil.sendPacket(serverPlayer, new DisableModPayload(true));
        } else {
            ServerNetworkUtil.sendPacket(serverPlayer, new ShulkerSupportPayload(true));
            ServerNetworkUtil.sendPacket(serverPlayer, new RefillSupportPayload(true));
        }
    }

    public void onLeave(ServerPlayer player) {
        playerSessions.remove(player);
    }

    public PlayerSession getSession(ServerPlayer player) {
        return playerSessions.get(player);
    }
}

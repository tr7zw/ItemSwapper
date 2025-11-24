package dev.tr7zw.itemswapper.mixin;

//? if >= 1.20.5 {

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.network.ServerCommonPacketListenerImpl;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

}
//? } else {
/*
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.ItemSwapperBase;
import dev.tr7zw.itemswapper.packets.RefillItemPayload;
import dev.tr7zw.itemswapper.packets.SwapItemPayload;
import dev.tr7zw.itemswapper.server.ItemSwapperSharedServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import dev.tr7zw.itemswapper.accessor.CustomPayloadLoader;

//spotless:off 
 //? if >= 1.20.2 {

  import net.minecraft.server.network.ServerCommonPacketListenerImpl;
  import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
 
  @Mixin(ServerCommonPacketListenerImpl.class)
 //? } else {
/^
  import org.spongepowered.asm.mixin.Shadow;
  import dev.tr7zw.itemswapper.legacy.CustomPacketPayload;
  import net.minecraft.server.network.ServerGamePacketListenerImpl;
  import net.minecraft.server.level.ServerPlayer;
  import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
 
  @Mixin(ServerGamePacketListenerImpl.class)
 ^///? }
 //spotless:on
public class ServerGamePacketListenerImplMixin {

    private static final ConfigManager configManager = ConfigManager.getInstance();
    // spotless:off 
 //? if <= 1.20.1 {
/^
       @Shadow
       public ServerPlayer player; 
 ^///? }
     //spotless:on

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        try {
            // spotless:off 
 //? if >= 1.20.2 {

              if ((Object) this instanceof ServerGamePacketListenerImpl gamePacketListener) {
                  // Don't apply this logic, if the server has the mod disabled.
                  if (!configManager.getConfig().serverPreventModUsage
                          && serverboundCustomPayloadPacket.payload() instanceof SwapItemPayload bytebuf) {
                      ItemSwapperSharedServer.INSTANCE.getItemHandler().swapItem(gamePacketListener.player, bytebuf);
                  }
                  if (!configManager.getConfig().serverPreventModUsage
                          && serverboundCustomPayloadPacket.payload() instanceof RefillItemPayload bytebuf) {
                      ItemSwapperSharedServer.INSTANCE.getItemHandler().refillSlot(gamePacketListener.player, bytebuf);
                  }
              }
 //? } else {
/^
                 CustomPacketPayload customPacketPayload = ((CustomPayloadLoader)serverboundCustomPayloadPacket).resolveObject(serverboundCustomPayloadPacket.getIdentifier(), serverboundCustomPayloadPacket.getData());
                     if (customPacketPayload instanceof SwapItemPayload bytebuf
                             && !configManager.getConfig().serverPreventModUsage) {
                         ItemSwapperSharedServer.INSTANCE.getItemHandler().swapItem(player, bytebuf);
                     }
                     if (customPacketPayload instanceof RefillItemPayload bytebuf
                            && !configManager.getConfig().serverPreventModUsage) {
                        ItemSwapperSharedServer.INSTANCE.getItemHandler().refillSlot(player, bytebuf);
                     }
 ^///? }
             //spotless:on
        } catch (Throwable th) {
            ItemSwapperBase.LOGGER.error("Error while processing packet!", th);
        }
    }

}
*///? }
   //spotless:on

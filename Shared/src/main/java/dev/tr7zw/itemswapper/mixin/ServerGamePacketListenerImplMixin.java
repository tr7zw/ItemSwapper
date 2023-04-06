package dev.tr7zw.itemswapper.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.util.NetworkUtil;
import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    private static final Logger network_logger = LogManager.getLogger("ItemSwapper-Network");
    private static final ConfigManager configManager = ConfigManager.getInstance();

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        // Don't apply this logic, if the server has the mod disabled.
        if (NetworkUtil.swapMessage.equals(serverboundCustomPayloadPacket.getIdentifier())
                && !configManager.getConfig().serverPreventModUsage) {
            swapItem(serverboundCustomPayloadPacket);
        }
        if (NetworkUtil.refillMessage.equals(serverboundCustomPayloadPacket.getIdentifier())
                && !configManager.getConfig().serverPreventModUsage) {
            refillSlot(serverboundCustomPayloadPacket);
        }
    }

    private void swapItem(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {
        try {
            FriendlyByteBuf buf = serverboundCustomPayloadPacket.getData();
            int inventory = buf.readInt();
            int slot = buf.readInt();
            ItemStack shulker = player.getInventory().items.get(inventory);
            NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
            if (content != null) {
                ItemStack tmp = content.get(slot);
                content.set(slot, player.getInventory().getSelected());
                player.getInventory().setItem(player.getInventory().selected, tmp);
                ShulkerHelper.setItem(shulker, content);
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

    private void refillSlot(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {
        try {
            FriendlyByteBuf buf = serverboundCustomPayloadPacket.getData();
            int targetSlot = buf.readInt();
            ItemStack target = player.getInventory().getItem(targetSlot);
            if(target == null || target.isEmpty()) {
                return;
            }
            int space = target.getMaxStackSize() - target.getCount();
            if(space <= 0) {
                // nothing to do
                return;
            }
            for(int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack shulker = player.getInventory().items.get(i);
                NonNullList<ItemStack> content = ShulkerHelper.getItems(shulker);
                if(content != null) {
                    boolean boxChanged = false;
                    for(int entry = 0; entry < content.size(); entry++) {
                        ItemStack boxItem = content.get(entry);
                        if(ItemStack.isSameItemSameTags(boxItem, target)) {
                            // same, use to restock
                            int amount = Math.min(space, boxItem.getCount());
                            target.setCount(target.getCount() + amount);
                            boxItem.setCount(boxItem.getCount() - amount);
                            space -= amount;
                            boxChanged = true;
                            if(space <= 0) {
                                break;
                            }
                        }
                    }
                    if(boxChanged) {
                        ShulkerHelper.setItem(shulker, content);
                    }
                }
            }
        } catch (Throwable th) {
            network_logger.error("Error handeling network packet!", th);
        }
    }

}

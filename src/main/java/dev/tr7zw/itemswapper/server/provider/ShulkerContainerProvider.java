package dev.tr7zw.itemswapper.server.provider;

import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.itemswapper.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;

public class ShulkerContainerProvider extends ListContainerProvider {

    private final static int SLOTS_PER_SHULKER = 27;

    @Override
    public Set<Item> getItemHandlers() {
        return ShulkerHelper.getShulkerItems();
    }

    @Override
    protected boolean isValidContainer(ServerPlayer player, ItemStack container) {
        return super.isValidContainer(player, container)
                && ItemSwapperSharedServer.INSTANCE.getPlayerManager().getSession(player).isShulkerSupport();
    }

    @Override
    public String getId() {
        return "itemswapper:shulker";
    }

    @Override
    public boolean canStoreinContainer(Item itemstack) {
        return !ShulkerHelper.isShulker(itemstack);
    }

    @Override
    public int getMaxSlots(ItemStack container) {
        return SLOTS_PER_SHULKER;
    }

    @Override
    protected NonNullList<ItemStack> getContent(ItemStack container) {
        return ShulkerHelper.getItems(container);
    }

    @Override
    protected void setContent(ItemStack container, NonNullList<ItemStack> content) {
        ShulkerHelper.setItem(container, content);
    }
}

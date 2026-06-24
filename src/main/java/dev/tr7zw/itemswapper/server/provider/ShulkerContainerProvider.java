package dev.tr7zw.itemswapper.server.provider;

import com.google.common.collect.*;
import dev.tr7zw.itemswapper.server.*;
import dev.tr7zw.itemswapper.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.stream.*;

public class ShulkerContainerProvider extends ListContainerProvider {

    private final static int SLOTS_PER_SHULKER = 27;
    //? if >= 26.2 {

    private static Set<Item> shulkers = Stream
            .concat(Stream.of(Items.SHULKER_BOX), Items.DYED_SHULKER_BOX.asList().stream()).collect(Collectors.toSet());
    //? } else {

    /*private static Set<Item> shulkers = Sets.newHashSet(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
            Items.RED_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX);
    *///? }

    @Override
    public Set<Item> getItemHandlers() {
        return shulkers;
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
        return !shulkers.contains(itemstack.asItem());
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

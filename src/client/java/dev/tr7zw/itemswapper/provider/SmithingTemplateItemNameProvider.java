package dev.tr7zw.itemswapper.provider;

import dev.tr7zw.itemswapper.accessor.SmithingTemplateItemAccessor;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

public class SmithingTemplateItemNameProvider implements NameProvider {

    @Override
    public boolean isProvider(ItemStack item) {
        return item.getItem() instanceof SmithingTemplateItem;
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        return ((SmithingTemplateItemAccessor) (Object) item.getItem()).getUpgradeDescription().plainCopy();
    }

}

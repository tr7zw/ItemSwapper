package dev.tr7zw.itemswapper.provider;

import java.util.HashSet;
import java.util.Set;

import dev.tr7zw.itemswapper.accessor.SmithingTemplateItemAccessor;
import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

public class SmithingTemplateItemNameProvider implements NameProvider {

    private Set<Item> templates = new HashSet<>();
    
    public SmithingTemplateItemNameProvider() {
        BuiltInRegistries.ITEM.forEach(i -> {
            if(i instanceof SmithingTemplateItem) {
                templates.add(i);
            }
        });
    }
    
    @Override
    public Set<Item> getItemHandlers() {
        return templates;
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        return ((SmithingTemplateItemAccessor)(Object)item.getItem()).getUpgradeDescription().plainCopy();
    }

}

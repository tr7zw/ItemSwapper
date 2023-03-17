package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public sealed interface Icon {

    public record ItemIcon(ItemStack item, Component nameOverwrite) implements Icon {
    };
    
    public record TextureIcon(ResourceLocation texture, Component name) implements Icon {
    };
    
    public record LinkIcon(ItemStack item, Component nameOverwrite, ResourceLocation nextId) implements Icon {
    };

}

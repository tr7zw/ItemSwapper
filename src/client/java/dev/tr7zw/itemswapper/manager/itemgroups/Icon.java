package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.ItemStack;

public sealed interface Icon {

    record ItemIcon(ItemStack item, Component nameOverwrite) implements Icon {
    };

    record TextureIcon(Identifier texture, Component name) implements Icon {
    };

    record LinkIcon(ItemStack item, Component nameOverwrite, Identifier nextId) implements Icon {
    };

}

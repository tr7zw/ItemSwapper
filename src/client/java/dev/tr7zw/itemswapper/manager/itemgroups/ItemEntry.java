package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public record ItemEntry(Item item, Identifier link, Component nameOverwride, boolean actAsLink) {

    public ItemEntry(Item item, Identifier link) {
        this(item, link, null, false);
    }

    public Item getItem() {
        return item;
    }

    public Identifier getLink() {
        return link;
    }

    public Component getNameOverwride() {
        return nameOverwride;
    }

    public boolean isActAsLink() {
        return actAsLink;
    }

}

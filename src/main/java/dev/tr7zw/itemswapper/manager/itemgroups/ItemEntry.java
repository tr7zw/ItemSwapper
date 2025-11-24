package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public class ItemEntry {

    private final Item item;
    private final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link;
    private final Component nameOverwride;
    private final boolean actAsLink;

    public ItemEntry(Item item, /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link) {
        this(item, link, null, false);
    }

    public ItemEntry(Item item, /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link,
            Component nameOverwride, boolean actAsLink) {
        this.item = item;
        this.link = link;
        this.nameOverwride = nameOverwride;
        this.actAsLink = actAsLink;
    }

    public Item getItem() {
        return item;
    }

    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getLink() {
        return link;
    }

    public Component getNameOverwride() {
        return nameOverwride;
    }

    public boolean isActAsLink() {
        return actAsLink;
    }

}

package dev.tr7zw.itemswapper.manager.itemgroups;

import java.util.Collections;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public class ItemList {

    private final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id;
    private final Component displayName;
    private final Item icon;
    private final Item[] items;
    private final Set<Item> openOnlyItems;
    private final Set<Item> ignoreItems;
    private final boolean disableAutoLink;
    private final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link;

    private ItemList(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.icon = builder.icon;
        this.items = builder.items;
        this.openOnlyItems = builder.openOnlyItems;
        this.ignoreItems = builder.ignoreItems;
        this.disableAutoLink = builder.disableAutoLink;
        this.link = builder.link;
    }

    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Item getIcon() {
        return icon;
    }

    public boolean isDisableAutoLink() {
        return disableAutoLink;
    }

    public Item[] getItems() {
        return items;
    }

    public Set<Item> getOpenOnlyItems() {
        return openOnlyItems;
    }

    public Set<Item> getIgnoreItems() {
        return ignoreItems;
    }

    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getLink() {
        return link;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id;
        private Component displayName;
        private Item icon;
        private Item[] items;
        private Set<Item> openOnlyItems = Collections.emptySet();
        private Set<Item> ignoreItems = Collections.emptySet();
        private boolean disableAutoLink;
        private/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link;

        private Builder() {
        }

        public Builder withId(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ id) {
            this.id = id;
            return this;
        }

        public Builder withDisplayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withIcon(Item icon) {
            this.icon = icon;
            return this;
        }

        public Builder withItems(Item[] items) {
            this.items = items;
            return this;
        }

        public Builder withOpenOnlyItems(Set<Item> openOnlyItems) {
            this.openOnlyItems = openOnlyItems;
            return this;
        }

        public Builder withIgnoreItems(Set<Item> ignoreItems) {
            this.ignoreItems = ignoreItems;
            return this;
        }

        public Builder withDisableAutoLink(boolean disableAutoLink) {
            this.disableAutoLink = disableAutoLink;
            return this;
        }

        public Builder withLink(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ link) {
            this.link = link;
            return this;
        }

        public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getId() {
            return id;
        }

        public Component getDisplayName() {
            return displayName;
        }

        public Item[] getItems() {
            return items;
        }

        public boolean isDisableAutoLink() {
            return disableAutoLink;
        }

        public ItemList build() {
            return new ItemList(this);
        }
    }

}

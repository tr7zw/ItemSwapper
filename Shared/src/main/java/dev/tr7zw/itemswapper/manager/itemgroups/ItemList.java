package dev.tr7zw.itemswapper.manager.itemgroups;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.util.Collections;

public class ItemList {

    private final ResourceLocation id;
    private final Component displayName;
    private final Item[] items;
    private final Set<Item> openOnlyItems;
    private final Set<Item> ignoreItems;
    private final boolean disableAutoLink;

    private ItemList(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.items = builder.items;
        this.openOnlyItems = builder.openOnlyItems;
        this.ignoreItems = builder.ignoreItems;
        this.disableAutoLink = builder.disableAutoLink;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ResourceLocation id;
        private Component displayName;
        private Item[] items;
        private Set<Item> openOnlyItems = Collections.emptySet();
        private Set<Item> ignoreItems = Collections.emptySet();
        private boolean disableAutoLink;

        private Builder() {
        }

        public Builder withId(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder withDisplayName(Component displayName) {
            this.displayName = displayName;
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

        public ResourceLocation getId() {
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

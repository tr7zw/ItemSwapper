package dev.tr7zw.itemswapper.manager.itemgroups;

import java.util.*;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public record ItemList(Identifier id, Component displayName, Item icon, Item[] items, Set<Item> openOnlyItems,
        Set<Item> ignoreItems, boolean disableAutoLink, Identifier link, List<Shortcut> shortcuts,
        boolean paletteList) {

    private ItemList(Builder builder) {
        this(builder.id, builder.displayName, builder.icon, builder.items, builder.openOnlyItems, builder.ignoreItems,
                builder.disableAutoLink, builder.link, builder.shortcuts, builder.paletteList);
    }

    public Identifier getId() {
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

    public Identifier getLink() {
        return link;
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public boolean isPaletteList() {
        return paletteList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Identifier id;
        private Component displayName;
        private Item icon;
        private Item[] items;
        private Set<Item> openOnlyItems = Collections.emptySet();
        private Set<Item> ignoreItems = Collections.emptySet();
        private boolean disableAutoLink;
        private Identifier link;
        private List<Shortcut> shortcuts = Collections.emptyList();
        private boolean paletteList = false;

        private Builder() {
        }

        public Builder withId(Identifier id) {
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

        public Builder withLink(Identifier link) {
            this.link = link;
            return this;
        }

        public Builder withShortcuts(List<Shortcut> shortcuts) {
            this.shortcuts = shortcuts;
            return this;
        }

        public Builder withPaletteList(boolean paletteList) {
            this.paletteList = paletteList;
            return this;
        }

        public Identifier getId() {
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

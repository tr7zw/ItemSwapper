package dev.tr7zw.itemswapper.manager.itemgroups;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

/**
 * Lombok afaik doesn't work for Loom, so using spark to generate the builder
 * 
 * @author tr7zw
 *
 */
public record ItemGroup(Identifier id, Component displayName, Item icon, int priority, boolean disableAutoLink,
        Identifier fallbackLink, Identifier forcedLink, ItemEntry[] items, Set<Item> openOnlyItems,
        Set<Item> ignoreItems, List<Shortcut> shortcuts) {

    private ItemGroup(Builder builder) {
        this(builder.id, builder.displayName, builder.icon, builder.priority, builder.disableAutoLink,
                builder.fallbackLink, builder.forcedLink, builder.items, builder.openOnlyItems, builder.ignoreItems,
                builder.shortcuts);
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

    public int getPriority() {
        return priority;
    }

    public Identifier getFallbackLink() {
        return fallbackLink;
    }

    public Identifier getForcedLink() {
        return forcedLink;
    }

    public ItemEntry[] getItems() {
        return items;
    }

    public ItemEntry getItem(int id) {
        if (id >= items.length) {
            return null;
        }
        return items[id];
    }

    public Set<Item> getOpenOnlyItems() {
        return openOnlyItems;
    }

    public Set<Item> getIgnoreItems() {
        return ignoreItems;
    }

    public boolean autoLinkDisabled() {
        return disableAutoLink;
    }

    public List<Shortcut> getShortcuts() {
        return shortcuts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Identifier id;
        private Component displayName;
        private Item icon;
        private int priority;
        private boolean disableAutoLink;
        private Identifier fallbackLink;
        private Identifier forcedLink;
        private ItemEntry[] items;
        private Set<Item> openOnlyItems = Collections.emptySet();
        private Set<Item> ignoreItems = Collections.emptySet();
        private List<Shortcut> shortcuts = Collections.emptyList();

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

        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder withDisableAutoLink(boolean disableAutoLink) {
            this.disableAutoLink = disableAutoLink;
            return this;
        }

        public Builder withFallbackLink(Identifier fallbackLink) {
            this.fallbackLink = fallbackLink;
            return this;
        }

        public Builder withForcedLink(Identifier forcedLink) {
            this.forcedLink = forcedLink;
            return this;
        }

        public Builder withItems(ItemEntry[] items) {
            this.items = items;
            return this;
        }

        public Builder withItems(Item[] items) {
            return withItems(toDefault(items));
        }

        private ItemEntry[] toDefault(Item[] items) {
            ItemEntry[] entries = new ItemEntry[items.length];
            for (int i = 0; i < items.length; i++) {
                entries[i] = new ItemEntry(items[i], null);
            }
            return entries;
        }

        public Builder withOpenOnlyItems(Set<Item> openOnlyItems) {
            this.openOnlyItems = openOnlyItems;
            return this;
        }

        public Builder withIgnoreItems(Set<Item> ignoreItems) {
            this.ignoreItems = ignoreItems;
            return this;
        }

        public Builder withShortcuts(List<Shortcut> shortcuts) {
            this.shortcuts = shortcuts;
            return this;
        }

        public Identifier getId() {
            return id;
        }

        public Component getDisplayName() {
            return displayName;
        }

        public int getPriority() {
            return priority;
        }

        public boolean isDisableAutoLink() {
            return disableAutoLink;
        }

        public Identifier getFallbackLink() {
            return fallbackLink;
        }

        public Identifier getForcedLink() {
            return forcedLink;
        }

        public ItemEntry[] getItems() {
            return items;
        }

        public Set<Item> getOpenOnlyItems() {
            return openOnlyItems;
        }

        public Set<Item> getIgnoreItems() {
            return ignoreItems;
        }

        public List<Shortcut> getShortcuts() {
            return shortcuts;
        }

        public ItemGroup build() {
            return new ItemGroup(this);
        }
    }

}

package dev.tr7zw.itemswapper.manager.itemgroups;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Lombok afaik doesn't work for Loom, so using spark to generate the builder
 * 
 * @author tr7zw
 *
 */
public class ItemGroup {

    private final ResourceLocation id;
    private final Component displayName;
    private final int priority;
    private final boolean disableAutoLink;
    private final ResourceLocation fallbackLink;
    private final ResourceLocation forcedLink;
    private final ItemEntry[] items;
    private final Set<Item> openOnlyItems;
    private final Set<Item> ignoreItems;
    private final List<Shortcut> shortcuts;

    private ItemGroup(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.priority = builder.priority;
        this.disableAutoLink = builder.disableAutoLink;
        this.fallbackLink = builder.fallbackLink;
        this.forcedLink = builder.forcedLink;
        this.items = builder.items;
        this.openOnlyItems = builder.openOnlyItems;
        this.ignoreItems = builder.ignoreItems;
        this.shortcuts = builder.shortcuts;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public int getPriority() {
        return priority;
    }

    public ResourceLocation getFallbackLink() {
        return fallbackLink;
    }

    public ResourceLocation getForcedLink() {
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
        private ResourceLocation id;
        private Component displayName;
        private int priority;
        private boolean disableAutoLink;
        private ResourceLocation fallbackLink;
        private ResourceLocation forcedLink;
        private ItemEntry[] items;
        private Set<Item> openOnlyItems = Collections.emptySet();
        private Set<Item> ignoreItems = Collections.emptySet();
        private List<Shortcut> shortcuts = Collections.emptyList();

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

        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder withDisableAutoLink(boolean disableAutoLink) {
            this.disableAutoLink = disableAutoLink;
            return this;
        }

        public Builder withFallbackLink(ResourceLocation fallbackLink) {
            this.fallbackLink = fallbackLink;
            return this;
        }

        public Builder withForcedLink(ResourceLocation forcedLink) {
            this.forcedLink = forcedLink;
            return this;
        }

        public Builder withItems(ItemEntry[] items) {
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

        public Builder withShortcuts(List<Shortcut> shortcuts) {
            this.shortcuts = shortcuts;
            return this;
        }

        public ItemGroup build() {
            return new ItemGroup(this);
        }

        public ResourceLocation getId() {
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

        public ResourceLocation getFallbackLink() {
            return fallbackLink;
        }

        public ResourceLocation getForcedLink() {
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

    }

}

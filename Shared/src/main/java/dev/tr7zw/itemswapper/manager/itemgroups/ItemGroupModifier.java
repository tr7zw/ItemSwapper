package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.resources.ResourceLocation;

public class ItemGroupModifier {

    private final ResourceLocation target;
    private final ItemEntry[] addItems;
    private final ItemEntry[] removeItems;

    private ItemGroupModifier(Builder builder) {
        this.target = builder.target;
        this.addItems = builder.addItems;
        this.removeItems = builder.removeItems;
    }
    
    public ResourceLocation getTarget() {
        return target;
    }

    public ItemEntry[] getAddItems() {
        return addItems;
    }

    public ItemEntry[] getRemoveItems() {
        return removeItems;
    }

    public static Builder builder() {
        return new Builder();
    }
    public static final class Builder {
        private ResourceLocation target;
        private ItemEntry[] addItems;
        private ItemEntry[] removeItems;

        private Builder() {
        }

        public Builder withTarget(ResourceLocation target) {
            this.target = target;
            return this;
        }

        public Builder withAddItems(ItemEntry[] addItems) {
            this.addItems = addItems;
            return this;
        }

        public Builder withRemoveItems(ItemEntry[] removeItems) {
            this.removeItems = removeItems;
            return this;
        }

        public ItemGroupModifier build() {
            return new ItemGroupModifier(this);
        }
    }
    
}

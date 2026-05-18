package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.resources.*;

public record ItemGroupModifier(Identifier target, ItemEntry[] addItems, ItemEntry[] removeItems) {

    private ItemGroupModifier(Builder builder) {
        this(builder.target, builder.addItems, builder.removeItems);
    }

    public Identifier getTarget() {
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
        private Identifier target;
        private ItemEntry[] addItems;
        private ItemEntry[] removeItems;

        private Builder() {
        }

        public Builder withTarget(Identifier target) {
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

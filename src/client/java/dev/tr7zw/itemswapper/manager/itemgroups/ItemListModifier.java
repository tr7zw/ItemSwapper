package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public record ItemListModifier(Identifier target, Item[] addItems, Item[] removeItems) {

    private ItemListModifier(Builder builder) {
        this(builder.target, builder.addItems, builder.removeItems);
    }

    public Identifier getTarget() {
        return target;
    }

    public Item[] getAddItems() {
        return addItems;
    }

    public Item[] getRemoveItems() {
        return removeItems;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Identifier target;
        private Item[] addItems;
        private Item[] removeItems;

        private Builder() {
        }

        public Builder withTarget(Identifier target) {
            this.target = target;
            return this;
        }

        public Builder withAddItems(Item[] addItems) {
            this.addItems = addItems;
            return this;
        }

        public Builder withRemoveItems(Item[] removeItems) {
            this.removeItems = removeItems;
            return this;
        }

        public ItemListModifier build() {
            return new ItemListModifier(this);
        }
    }

}

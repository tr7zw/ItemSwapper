package dev.tr7zw.itemswapper.manager.itemgroups;

import net.minecraft.resources.*;
import net.minecraft.world.item.Item;

public class ItemListModifier {

    private final/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ target;
    private final Item[] addItems;
    private final Item[] removeItems;

    private ItemListModifier(Builder builder) {
        this.target = builder.target;
        this.addItems = builder.addItems;
        this.removeItems = builder.removeItems;
    }

    public/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ getTarget() {
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
        private/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ target;
        private Item[] addItems;
        private Item[] removeItems;

        private Builder() {
        }

        public Builder withTarget(/*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ target) {
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

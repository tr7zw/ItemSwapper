package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
//? if < 1.21.2 {
/*
import org.spongepowered.asm.mixin.Shadow;
*///? }

import dev.tr7zw.itemswapper.accessor.SmithingTemplateItemAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SmithingTemplateItem;

@Mixin(SmithingTemplateItem.class)
public class SmithingTemplateItemMixin implements SmithingTemplateItemAccessor {

    //? if < 1.21.2 {
/*
    @Shadow
    *///? }
    private Component upgradeDescription;

    @Override
    public Component getUpgradeDescription() {
        return upgradeDescription;
    }

}

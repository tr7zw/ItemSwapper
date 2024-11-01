package dev.tr7zw.itemswapper.mixin;

import org.spongepowered.asm.mixin.Mixin;
//spotless:off
//#if MC < 12102
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif
//spotless:on

import dev.tr7zw.itemswapper.accessor.SmithingTemplateItemAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SmithingTemplateItem;

@Mixin(SmithingTemplateItem.class)
public class SmithingTemplateItemMixin implements SmithingTemplateItemAccessor {

    // spotless:off
    //#if MC < 12102
    //$$    @Shadow
    //#endif
    //spotless:on
    private Component upgradeDescription;

    @Override
    public Component getUpgradeDescription() {
        return upgradeDescription;
    }

}

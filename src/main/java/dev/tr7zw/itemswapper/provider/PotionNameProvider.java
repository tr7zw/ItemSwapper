package dev.tr7zw.itemswapper.provider;

import java.util.Set;

import com.google.common.collect.Sets;

import dev.tr7zw.itemswapper.api.client.NameProvider;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if < 1.20.5 {
/*
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.ComponentContents;
import java.util.List;
*///? }

public class PotionNameProvider implements NameProvider {

    private static Set<Item> potions = Sets.newHashSet(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION,
            Items.TIPPED_ARROW);

    @Override
    public boolean isProvider(ItemStack item) {
        return potions.contains(item.getItem());
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        //? if < 1.20.5 {
/*
        List<MobEffectInstance> effects = PotionUtils.getPotion(item).getEffects();
        if (!effects.isEmpty()) {
            MutableComponent comp = formatEffect(effects.get(0));
            if (effects.size() >= 2) {
                comp.append(", ").append(formatEffect(effects.get(1)));
            }
            return comp;
        }
        *///? } else {
        
        net.minecraft.world.item.alchemy.PotionContents potionContents = item
                .get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
        if (potionContents != null && potionContents.getAllEffects().iterator().hasNext()) {
            MutableComponent effects = ComponentProvider.empty();
            boolean b = false;
            for (MobEffectInstance mobEffectInstance : potionContents.getAllEffects()) {
                if (!b) {
                    b = true;
                } else {
                    effects.append(", ");
                }
                effects.append(Component.translatable(mobEffectInstance.getDescriptionId()));
                if (mobEffectInstance.getAmplifier() > 0) {
                    effects.append(Component.literal(" "));
                    effects.append(Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()));
                }
        
                if (!mobEffectInstance.endsWithin(20)) {
                    effects = Component.translatable("potion.withDuration",
                            new Object[] { effects, MobEffectUtil.formatDuration(mobEffectInstance, 1, 20) });
                }
            }
            return effects;
        }
        //? }
        return item.getHoverName();
    }

    //? if < 1.20.5 {
/*
    private MutableComponent formatEffect(MobEffectInstance effect) {
        MutableComponent comp = ComponentProvider.empty().append(effect.getEffect().getDisplayName());
        if (effect.getAmplifier() > 1) {
            comp.append(" ").append(ComponentProvider.translatable("potion.potency." + effect.getAmplifier()));
        }
        if (effect.getDuration() > 1) {
            //?if >= 1.20.4 {
            comp.append(" (").append(ComponentProvider.literal(StringUtil.formatTickDuration(effect.getDuration(), 20)))
                    .append(")");
            //? } else {
            /^comp.append(" (").append(ComponentProvider.literal(StringUtil.formatTickDuration(effect.getDuration())))
                    .append(")");
            ^///? }
        }
        return comp;
    }
    *///? }

}

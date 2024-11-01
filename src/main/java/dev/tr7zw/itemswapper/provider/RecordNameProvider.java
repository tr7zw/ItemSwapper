package dev.tr7zw.itemswapper.provider;

import java.util.Optional;

import dev.tr7zw.itemswapper.api.client.NameProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

//spotless:off 
//#if MC >= 12100
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
//#else
//$$ import net.minecraft.tags.ItemTags;
//#endif
//spotless:on

public class RecordNameProvider implements NameProvider {

    @Override
    public boolean isProvider(ItemStack item) {
        // spotless:off 
        //#if MC >= 12100
        return item.getComponents().has(DataComponents.JUKEBOX_PLAYABLE);
        //#else
        //$$ return item.is(ItemTags.MUSIC_DISCS);
        //#endif
        //spotless:on
    }

    @Override
    public Component getDisplayName(ItemStack item) {
        // spotless:off 
        //#if MC >= 12102
        JukeboxPlayable data = item.getComponents().get(DataComponents.JUKEBOX_PLAYABLE);
        Optional<Holder<JukeboxSong>> holder = data.song().unwrap(Minecraft.getInstance().level.registryAccess());
        if(holder.isPresent()){
            MutableComponent mutableComponent = ((JukeboxSong) holder.get().value()).description().copy();
            ComponentUtils.mergeStyles(mutableComponent, Style.EMPTY.withColor(ChatFormatting.GRAY));
            return mutableComponent;
        };
        return item.getStyledHoverName();
        //#else
        //$$ return dev.tr7zw.util.ComponentProvider.translatable(item.getItem().getDescriptionId() + ".desc");
        //#endif
        //spotless:on
    }

}

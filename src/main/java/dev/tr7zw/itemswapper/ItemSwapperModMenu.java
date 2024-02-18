package dev.tr7zw.itemswapper;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ItemSwapperModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // the cleaner method didn't behave correctly, sometimes being null
        return screen -> ItemSwapperMod.instance.createConfigScreen(screen);
    }

}

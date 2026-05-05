package dev.tr7zw.itemswapper;

import dev.tr7zw.itemswapper.api.client.*;
import dev.tr7zw.itemswapper.config.*;
import dev.tr7zw.itemswapper.manager.*;
import dev.tr7zw.transition.config.*;
import dev.tr7zw.transition.mc.*;

import dev.tr7zw.itemswapper.manager.ItemGroupManager.Page;
import dev.tr7zw.itemswapper.provider.InstrumentItemNameProvider;
import dev.tr7zw.itemswapper.provider.PotionNameProvider;
import dev.tr7zw.itemswapper.provider.RecordNameProvider;
import lombok.Getter;
import net.minecraft.world.item.Item;

public abstract class ItemSwapperSharedMod extends ItemSwapperBase {

    public static ItemSwapperSharedMod instance;

    @Getter
    private final ClientSessionSettings sessionSettings = new ClientSessionSettings();
    @Getter
    private final ItemGroupManager itemGroupManager = new ItemGroupManager();
    @Getter
    private final ClientProviderManager clientProviderManager = new ClientProviderManager(sessionSettings);
    @Getter
    private final BlockTextureManager blockTextureManager = new BlockTextureManager();
    @Getter
    private final ItemManager itemManager = new ItemManager(clientProviderManager, ItemSwapperClientAPI.getInstance());
    private final ConfigManager<Config> configManager = ConfigHolder.getInstance().getGeneral();
    private final ConfigManager<CacheServerAddresses> serverCache = ConfigHolder.getInstance().getServerCache();
    @Getter
    protected final ClientUiManager clientUiManager = new ClientUiManager(itemGroupManager, configManager,
            sessionSettings, serverCache);

    private boolean lateInitCompleted = false;

    public void init() {
        instance = this;
        LOGGER.info("Loading ItemSwapper!");

        initModloader();
    }

    private void lateInit() {
        clientProviderManager.registerNameProvider(new PotionNameProvider());
        clientProviderManager.registerNameProvider(new InstrumentItemNameProvider());
        clientProviderManager.registerNameProvider(new RecordNameProvider());

        //? if < 1.21.2 {

        /*clientProviderManager
                .registerNameProvider(new dev.tr7zw.itemswapper.provider.SmithingTemplateItemNameProvider());
        *///? }
    }

    public void clientTick() {
        // run this code later, so all other mods are done loading
        if (!lateInitCompleted) {
            lateInitCompleted = true;
            lateInit();
        }
        clientUiManager.clientTick();
    }

    public abstract void initModloader();

    public Item getLastItem() {
        return sessionSettings.getLastItem();
    }

    public void setLastItem(Item lastItem) {
        sessionSettings.setLastItem(lastItem);
    }

    public Page getLastPage() {
        return sessionSettings.getLastPage();
    }

    public void setLastPage(Page lastPage) {
        sessionSettings.setLastPage(lastPage);
    }

}

package dev.tr7zw.itemswapper.api.server;

public class ItemSwapperServerAPI {

    private static final ItemSwapperServerAPI INSTANCE = new ItemSwapperServerAPI();

    private ItemSwapperServerAPI() {

    }

    public static ItemSwapperServerAPI getInstance() {
        return INSTANCE;
    }

}

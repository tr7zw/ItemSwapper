package dev.tr7zw.itemswapper.api.client;

import java.util.concurrent.atomic.AtomicBoolean;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.Event;
import dev.tr7zw.itemswapper.api.EventFactory;

public class ItemSwapperClientAPI {

    private static final ItemSwapperClientAPI INSTANCE = new ItemSwapperClientAPI();
    private final ItemSwapperSharedMod modInstance = ItemSwapperSharedMod.instance;
    public final Event<OnSwap> prepareItemSwapEvent = EventFactory.createEvent();
    public final Event<SwapSent> itemSwapSentEvent = EventFactory.createEvent();

    private ItemSwapperClientAPI() {

    }

    public static ItemSwapperClientAPI getInstance() {
        return INSTANCE;
    }

    /**
     * These providers will be called before the inventory gets checked. Example
     * usecase: systems that suck up picked up items, and that now should get used
     * up before the inventory.
     * 
     * @param provider
     */
    public void registerEarlyItemProvider(ItemProvider provider) {
        modInstance.getClientProviderManager().registerEarlyItemProvider(provider);
    }

    /**
     * These providers will be called after the inventory gets checked. Example
     * usecase: Checking remote storage like enderchests/refined storage
     * 
     * @param provider
     */
    public void registerLateItemProvider(ItemProvider provider) {
        modInstance.getClientProviderManager().registerLateItemProvider(provider);
    }

    /**
     * Register a {@link ContainerProvider} that can grab items out of containers
     * inside the players inventory
     * 
     * @param provider
     */
    public void registerContainerProvider(ContainerProvider provider) {
        modInstance.getClientProviderManager().registerContainerProvider(provider);
    }

    public void registerNameProvider(NameProvider provider) {
        modInstance.getClientProviderManager().registerNameProvider(provider);
    }

    public record OnSwap(AvailableSlot slot, AtomicBoolean canceled) {

    }

    public record SwapSent(AvailableSlot slot) {

    }

}

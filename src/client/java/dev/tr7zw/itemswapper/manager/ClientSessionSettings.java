package dev.tr7zw.itemswapper.manager;

import dev.tr7zw.itemswapper.packets.*;
import lombok.*;
import net.minecraft.world.item.*;

import java.util.*;

@Getter
@Setter
public class ClientSessionSettings {
    private boolean enableShulkers = false;
    private boolean enableRefill = false;
    private boolean modDisabled = false;
    private boolean disabledByPlayer = false;
    private boolean bypassAccepted = false;
    private final Map<Item, RemoteItem> remoteItemInfo = new HashMap<>();
    private final List<RemoteItem> lastReceivedItems = new ArrayList<>();
    private Item lastItem;
    private ItemGroupManager.Page lastPage;

    public void reset() {
        enableShulkers = false;
        enableRefill = false;
        modDisabled = false;
        disabledByPlayer = false;
        bypassAccepted = false;
        remoteItemInfo.clear();
        lastReceivedItems.clear();
    }

    public void updateItemInfo(List<RemoteItem> items) {
        remoteItemInfo.clear();
        for (RemoteItem item : items) {
            remoteItemInfo.put(item.itemStack().getItem(), item);
        }
        lastReceivedItems.clear();
        lastReceivedItems.addAll(items);
    }
}

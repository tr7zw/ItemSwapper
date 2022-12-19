package dev.tr7zw.itemswapper.api;

import java.util.function.Consumer;

public interface Event<T> {

    public void register(Consumer<T> handler);
    
    public T callEvent(T event);
    
}

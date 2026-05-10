package dev.tr7zw.itemswapper.server;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class PlayerSession {

    private final Map<String, Object> data = new HashMap<>();
    private boolean keepLastItem = true;
    private boolean shulkerSupport = true;

}

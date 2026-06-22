package dev.tr7zw.itemswapper.overlay.logic;

import lombok.*;
import net.minecraft.resources.*;

@Getter
@Setter
public class WidgetArea {
    private int x;
    private int y;
    private int backgroundSizeX;
    private int backgroundSizeY;
    private int backgroundTextureSizeX;
    private int backgroundTextureSizeY;
    private Identifier backgroundTexture;
    private int mouseBoundsX;
    private int mouseBoundsY;

    public WidgetArea(int backgroundSizeX, int backgroundSizeY, int backgroundTextureSizeX, int backgroundTextureSizeY,
            Identifier backgroundTexture, int mouseBoundsX, int mouseBoundsY) {
        this.backgroundSizeX = backgroundSizeX;
        this.backgroundSizeY = backgroundSizeY;
        this.backgroundTextureSizeX = backgroundTextureSizeX;
        this.backgroundTextureSizeY = backgroundTextureSizeY;
        this.backgroundTexture = backgroundTexture;
        this.mouseBoundsX = mouseBoundsX;
        this.mouseBoundsY = mouseBoundsY;
    }

}

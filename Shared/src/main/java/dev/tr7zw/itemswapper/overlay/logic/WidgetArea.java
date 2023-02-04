package dev.tr7zw.itemswapper.overlay.logic;

import net.minecraft.resources.ResourceLocation;

public class WidgetArea {
    private int x;
    private int y;
    private int backgroundSizeX;
    private int backgroundSizeY;
    private int backgroundTextureSizeX;
    private int backgroundTextureSizeY;
    private ResourceLocation backgroundTexture;
    private int mouseBoundsX;
    private int mouseBoundsY;

    public WidgetArea(int backgroundSizeX, int backgroundSizeY, int backgroundTextureSizeX,
            int backgroundTextureSizeY, ResourceLocation backgroundTexture, int mouseBoundsX, int mouseBoundsY) {
        this.backgroundSizeX = backgroundSizeX;
        this.backgroundSizeY = backgroundSizeY;
        this.backgroundTextureSizeX = backgroundTextureSizeX;
        this.backgroundTextureSizeY = backgroundTextureSizeY;
        this.backgroundTexture = backgroundTexture;
        this.mouseBoundsX = mouseBoundsX;
        this.mouseBoundsY = mouseBoundsY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBackgroundSizeX() {
        return backgroundSizeX;
    }

    public void setBackgroundSizeX(int backgroundSizeX) {
        this.backgroundSizeX = backgroundSizeX;
    }

    public int getBackgroundSizeY() {
        return backgroundSizeY;
    }

    public void setBackgroundSizeY(int backgroundSizeY) {
        this.backgroundSizeY = backgroundSizeY;
    }

    public int getBackgroundTextureSizeX() {
        return backgroundTextureSizeX;
    }

    public void setBackgroundTextureSizeX(int backgroundTextureSizeX) {
        this.backgroundTextureSizeX = backgroundTextureSizeX;
    }

    public int getBackgroundTextureSizeY() {
        return backgroundTextureSizeY;
    }

    public void setBackgroundTextureSizeY(int backgroundTextureSizeY) {
        this.backgroundTextureSizeY = backgroundTextureSizeY;
    }

    public ResourceLocation getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public int getMouseBoundsX() {
        return mouseBoundsX;
    }

    public void setMouseBoundsX(int mouseBoundsX) {
        this.mouseBoundsX = mouseBoundsX;
    }

    public int getMouseBoundsY() {
        return mouseBoundsY;
    }

    public void setMouseBoundsY(int mouseBoundsY) {
        this.mouseBoundsY = mouseBoundsY;
    }

}
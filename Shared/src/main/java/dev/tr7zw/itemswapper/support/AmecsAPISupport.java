package dev.tr7zw.itemswapper.support;

/**
 * This is a stupid hack and might need to be fixed with help of the Amecs-API
 * dev
 * 
 * @author tr7zw
 *
 */
public class AmecsAPISupport {

    private static final AmecsAPISupport INSTANCE = new AmecsAPISupport();

    private boolean isActive = false;

    public void init() {
        isActive = true;
    }

    public static AmecsAPISupport getInstance() {
        return INSTANCE;
    }

    public boolean isActive() {
        return isActive;
    }

}

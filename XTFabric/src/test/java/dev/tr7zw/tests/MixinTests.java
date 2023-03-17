package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.itemswapper.ItemSwapperMod;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.locale.Language;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.RecordItem;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        objenesis.newInstance(ContainerScreen.class);
        objenesis.newInstance(ClientLevel.class);
        objenesis.newInstance(ClientPacketListener.class);
        objenesis.newInstance(InstrumentItem.class);
        objenesis.newInstance(Minecraft.class);
        objenesis.newInstance(MouseHandler.class);
        objenesis.newInstance(IntegratedPlayerList.class);
        objenesis.newInstance(RecordItem.class);
        objenesis.newInstance(ServerGamePacketListenerImpl.class);
    }
    
    @Test
    public void langTests() throws Throwable {
        Language lang = TestUtil.loadDefault("/assets/itemswapper/lang/en_us.json");
        ItemSwapperMod.instance = new TestMod();
        CustomConfigScreen screen = (CustomConfigScreen) ItemSwapperMod.instance.createConfigScreen(null);
        List<OptionInstance<?>> options = TestUtil.bootStrapCustomConfigScreen(screen);
        assertNotEquals(screen.getTitle().getString(), lang.getOrDefault(screen.getTitle().getString()));
        for(OptionInstance<?> option : options) {
            Set<String> keys = TestUtil.getKeys(option, true);
            for(String key : keys) {
                System.out.println(key + " " + lang.getOrDefault(key));
                assertNotEquals(key, lang.getOrDefault(key));
            }
        }
    }

}
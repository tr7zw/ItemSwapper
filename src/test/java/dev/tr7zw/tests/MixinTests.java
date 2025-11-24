package dev.tr7zw.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.InstrumentItem;

//? if >= 1.21.0 {

import net.minecraft.world.item.Items;
//? } else {
/*
import net.minecraft.world.item.RecordItem;
*///? }

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
        //? if >= 1.21.0 {

        objenesis.newInstance(Items.class);
        //? } else {
        /*
        objenesis.newInstance(RecordItem.class);
        *///? }
        objenesis.newInstance(ServerGamePacketListenerImpl.class);
    }

}

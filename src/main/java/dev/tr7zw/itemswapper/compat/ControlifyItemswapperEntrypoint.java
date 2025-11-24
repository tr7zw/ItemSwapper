package dev.tr7zw.itemswapper.compat;

import org.joml.Vector2d;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUIAbstractInput;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;
//? if = 1.20.2 || < 1.20.0 {
/*
import dev.isxander.controlify.controller.Controller;
*///? } else {

import dev.isxander.controlify.controller.ControllerEntity;
//? }

public class ControlifyItemswapperEntrypoint implements ControlifyEntrypoint {

    //? if >= 1.21.10 {
    
    @Override
    public void onControlifyInit(dev.isxander.controlify.api.entrypoint.InitContext arg0) {
    }
    
    @Override
    public void onControlifyPreInit(dev.isxander.controlify.api.entrypoint.PreInitContext arg0) {
        ControlifySupport.getInstance().init();
        ScreenProcessorProvider.registerProvider(SwitchItemOverlay.class, ItemSwapperControlifyProcessor::new);
        ScreenProcessorProvider.registerProvider(ItemListOverlay.class, ItemSwapperControlifyProcessor::new);
    }
    //? }

    //? if >= 1.20.0 && < 1.21.10 {
    /*
     @Override
     public void onControlifyPreInit(ControlifyApi arg0) {
         ControlifySupport.getInstance().init();
         ScreenProcessorProvider.registerProvider(SwitchItemOverlay.class, ItemSwapperControlifyProcessor::new);
         ScreenProcessorProvider.registerProvider(ItemListOverlay.class, ItemSwapperControlifyProcessor::new);
     }
    *///? } else if < 1.20.10 {
/*
    @Override
    public void onControlifyPreInit(ControlifyApi arg0) {
        ControlifySupport.getInstance().init();
        ScreenProcessorProvider.REGISTRY.register(SwitchItemOverlay.class, ItemSwapperControlifyProcessor::new);
        ScreenProcessorProvider.REGISTRY.register(ItemListOverlay.class, ItemSwapperControlifyProcessor::new);
    }
    *///? }

    @Override
    public void onControllersDiscovered(ControlifyApi arg0) {

    }

    public class ItemSwapperControlifyProcessor<T extends ItemSwapperUIAbstractInput> extends ScreenProcessor<T> {

        private SnapPoint snapPoint = null;

        public ItemSwapperControlifyProcessor(T screen) {
            super(screen);
        }

        private void handleMouseTeleport(int x, int y) {
            snapPoint = new SnapPoint(x, y, 1);
        }

        @Override
        // why does 1.20.2 not have the 2.0 update?
        //? if = 1.20.2 || < 1.20.0 {
/*
        protected void handleScreenVMouse(Controller<?, ?> controller, VirtualMouseHandler vmouse) {
            *///? } else {
            
                    protected void handleScreenVMouse(ControllerEntity controller, VirtualMouseHandler vmouse) {
            //? }
            super.handleScreenVMouse(controller, vmouse);
            screen.registerVCursorHandler(this::handleMouseTeleport);
            if (snapPoint != null) {
                // 1.19 Controlify is not getting update and missing this method
                //? if >= 1.20.0 {
                
                vmouse.snapToPoint(snapPoint, new Vector2d(1));
                //? }
                snapPoint = null;
            }
        }

        @Override
        public VirtualMouseBehaviour virtualMouseBehaviour() {
            return VirtualMouseBehaviour.ENABLED;
        }
    }

}

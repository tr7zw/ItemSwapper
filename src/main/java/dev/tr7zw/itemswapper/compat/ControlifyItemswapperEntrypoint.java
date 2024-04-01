package dev.tr7zw.itemswapper.compat;

import org.joml.Vector2d;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import dev.isxander.controlify.controller.Controller;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import dev.tr7zw.itemswapper.overlay.ItemListOverlay;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUIAbstractInput;
import dev.tr7zw.itemswapper.overlay.SwitchItemOverlay;

public class ControlifyItemswapperEntrypoint implements ControlifyEntrypoint {

    @Override
    public void onControlifyPreInit(ControlifyApi arg0) {
        ControlifySupport.getInstance().init();
        // spotless:off 
        //#if MC >= 12002
        ScreenProcessorProvider.registerProvider(SwitchItemOverlay.class, ItemSwapperControlifyProcessor::new);
        ScreenProcessorProvider.registerProvider(ItemListOverlay.class, ItemSwapperControlifyProcessor::new);
        //#else
        //$$ ScreenProcessorProvider.REGISTRY.register(
      //$$         SwitchItemOverlay.class,
      //$$         ItemSwapperControlifyProcessor::new
      //$$     );
      //$$ ScreenProcessorProvider.REGISTRY.register(
      //$$         ItemListOverlay.class,
      //$$         ItemSwapperControlifyProcessor::new
      //$$    );
        //#endif
        //spotless:on
    }

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
        protected void handleScreenVMouse(Controller<?, ?> controller, VirtualMouseHandler vmouse) {
            super.handleScreenVMouse(controller, vmouse);
            screen.registerVCursorHandler(this::handleMouseTeleport);
            if (snapPoint != null) {
                // 1.20.1 Controlify is missing this method. isxander pls fix
                // spotless:off 
                //#if MC >= 12002
                vmouse.snapToPoint(snapPoint, new Vector2d(1));
                //#endif
                //spotless:on
                snapPoint = null;
            }
        }

        @Override
        public VirtualMouseBehaviour virtualMouseBehaviour() {
            return VirtualMouseBehaviour.ENABLED;
        }
    }

}

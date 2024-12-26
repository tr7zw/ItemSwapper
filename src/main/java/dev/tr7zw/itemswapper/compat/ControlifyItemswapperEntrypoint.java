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
//#if MC == 12002 || MC < 12000
//$$ import dev.isxander.controlify.controller.Controller;
//#else
import dev.isxander.controlify.controller.ControllerEntity;
//#endif

public class ControlifyItemswapperEntrypoint implements ControlifyEntrypoint {

    @Override
    public void onControlifyPreInit(ControlifyApi arg0) {
        ControlifySupport.getInstance().init();
        //#if MC >= 12000
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
        // why does 1.20.2 not have the 2.0 update?
        //#if MC == 12002 || MC < 12000
        //$$ protected void handleScreenVMouse(Controller<?, ?> controller, VirtualMouseHandler vmouse) {
        //#else
        protected void handleScreenVMouse(ControllerEntity controller, VirtualMouseHandler vmouse) {
            //#endif
            super.handleScreenVMouse(controller, vmouse);
            screen.registerVCursorHandler(this::handleMouseTeleport);
            if (snapPoint != null) {
                // 1.19 Controlify is not getting update and missing this method
                //#if MC >= 12000
                vmouse.snapToPoint(snapPoint, new Vector2d(1));
                //#endif
                snapPoint = null;
            }
        }

        @Override
        public VirtualMouseBehaviour virtualMouseBehaviour() {
            return VirtualMouseBehaviour.ENABLED;
        }
    }

}

package dev.tr7zw.itemswapper.compat;

import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.config.ConfigManager;
import dev.tr7zw.itemswapper.overlay.ItemSwapperUI;
import eu.midnightdust.midnightcontrols.client.ButtonState;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.compat.CompatHandler;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.controller.PressAction;
import net.minecraft.client.Minecraft;

public class MidnightControllsSupport implements CompatHandler{
    
    private final ConfigManager configManager = ConfigManager.getInstance();
    private ButtonCategory CATEGORY;
    
    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        CATEGORY = InputManager.registerCategory(new Identifier("itemswapper:controlls"));
        new ButtonBinding.Builder("key.itemswapper.itemswitcher")
        .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)
        .category(CATEGORY)
        .linkKeybind(ItemSwapperSharedMod.instance.getKeybind())
        .filter((client, buttonBinding) -> client.screen == null || client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                // remove screen check
                button.asKeyBinding().ifPresent(binding -> binding.setDown(button.isButtonDown()));
                return true;
            }
        })
        .register();
        
        new ButtonBinding.Builder("key.itemswapper.toggleitem")
        .buttons(GLFW.GLFW_GAMEPAD_BUTTON_X)
        .category(CATEGORY)
        .linkKeybind(ItemSwapperSharedMod.instance.getKeybind())
        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                // remove screen check
                if(action == ButtonState.PRESS && client.screen instanceof ItemSwapperUI ui) {
                    ui.handleSwitchSelection();
                }
                return false;
            }
        })
        .register();
        
        new ButtonBinding.Builder("key.itemswapper.moveup")
        .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, false))
        .category(CATEGORY)
        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                if(button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                    ui.handleInput(0, -paramFloat * mouseAcceleration());
                    return true;
                }
                return false;
            }
        })
        .register();
        
        new ButtonBinding.Builder("key.itemswapper.movedown")
        .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, true))
        .category(CATEGORY)
        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                if(button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                    ui.handleInput(0, paramFloat * mouseAcceleration());
                    return true;
                }
                return false;
            }
        })
        .register();
        
        new ButtonBinding.Builder("key.itemswapper.moveleft")
        .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, true))
        .category(CATEGORY)
        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                if(button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                    ui.handleInput(paramFloat * mouseAcceleration(), 0);
                    return true;
                }
                return false;
            }
        })
        .register();
        
        new ButtonBinding.Builder("key.itemswapper.moveright")
        .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, false))
        .category(CATEGORY)
        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
        .action(new PressAction() {
            
            @Override
            public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                    @NotNull ButtonState action) {
                if(button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                    ui.handleInput(-paramFloat * mouseAcceleration(), 0);
                    return true;
                }
                return false;
            }
        })
        .register();
        
    }
    
    private float mouseAcceleration() {
        return configManager.getConfig().controllerSpeed;
    }

}

package dev.tr7zw.itemswapper.compat;

import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.ItemSwapperUI;
import dev.tr7zw.itemswapper.config.ConfigManager;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.compat.CompatHandler;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.controller.PressAction;
//? if >= 1.20.3 || = 1.20.1 {

import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
//? } else {
/*
import eu.midnightdust.midnightcontrols.client.ButtonState;
*///? }
import net.minecraft.client.Minecraft;

public class MidnightControllsSupport implements CompatHandler {

    private final ConfigManager configManager = ConfigManager.getInstance();
    private ButtonCategory CATEGORY;
    private Minecraft client = Minecraft.getInstance();

    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        CATEGORY = InputManager.registerCategory(new Identifier("itemswapper:controlls"));
        new ButtonBinding.Builder("key.itemswapper.itemswitcher").buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)
                .category(CATEGORY).linkKeybind(ItemSwapperSharedMod.instance.getKeybind())
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen == null || client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen == null || client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        // remove screen check
                        button.asKeyBinding().ifPresent(binding -> binding.setDown(button.isButtonDown()));
                        return true;
                    }
                }).register();

        new ButtonBinding.Builder("key.itemswapper.toggleitem").buttons(GLFW.GLFW_GAMEPAD_BUTTON_X).category(CATEGORY)
                .linkKeybind(ItemSwapperSharedMod.instance.getKeybind())
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        // remove screen check
                        if (action == ButtonState.PRESS && client.screen instanceof ItemSwapperUI ui) {
                            ui.onSecondaryClick();
                        }
                        return false;
                    }
                }).register();

        new ButtonBinding.Builder("key.itemswapper.moveup")
                .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, false)).category(CATEGORY)
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        if (button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                            ui.handleInput(0, -paramFloat * mouseAcceleration());
                            return true;
                        }
                        return false;
                    }
                }).register();

        new ButtonBinding.Builder("key.itemswapper.movedown")
                .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, true)).category(CATEGORY)
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        if (button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                            ui.handleInput(0, paramFloat * mouseAcceleration());
                            return true;
                        }
                        return false;
                    }
                }).register();

        new ButtonBinding.Builder("key.itemswapper.moveleft")
                .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, true)).category(CATEGORY)
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        if (button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                            ui.handleInput(paramFloat * mouseAcceleration(), 0);
                            return true;
                        }
                        return false;
                    }
                }).register();

        new ButtonBinding.Builder("key.itemswapper.moveright")
                .buttons(ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, false)).category(CATEGORY)
                //? if >= 1.21.0 {

                .filter((buttonBinding) -> client.screen instanceof ItemSwapperUI)
                //? } else {
                /*
                        .filter((client, buttonBinding) -> client.screen instanceof ItemSwapperUI)
                        *///? }
                .action(new PressAction() {

                    @Override
                    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float paramFloat,
                            @NotNull ButtonState action) {
                        if (button.isButtonDown() && client.screen instanceof ItemSwapperUI ui) {
                            ui.handleInput(-paramFloat * mouseAcceleration(), 0);
                            return true;
                        }
                        return false;
                    }
                }).register();

    }

    private float mouseAcceleration() {
        return configManager.getConfig().controllerSpeed;
    }

}

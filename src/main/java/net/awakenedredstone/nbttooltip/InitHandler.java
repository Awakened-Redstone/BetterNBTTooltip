package net.awakenedredstone.nbttooltip;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.awakenedredstone.nbttooltip.util.ScrollUtil;
import net.minecraft.client.MinecraftClient;
import net.awakenedredstone.nbttooltip.config.Callbacks;
import net.awakenedredstone.nbttooltip.config.Configs;
import net.awakenedredstone.nbttooltip.config.InputHandler;

public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler("nbttooltip", new Configs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(ScrollUtil.getInstance());
        Callbacks.init(MinecraftClient.getInstance());
    }
}

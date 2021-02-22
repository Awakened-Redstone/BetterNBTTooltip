package net.awakenedredstone.nbttooltip.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.awakenedredstone.nbttooltip.util.KeybindCallbacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.awakenedredstone.nbttooltip.gui.GuiConfigs;

public class Callbacks {

    private static MinecraftClient mc_;

    public static void init(MinecraftClient mc) {
        mc_ = mc;
        ChangeCallback changeCallback = new ChangeCallback();
        KeybindCallbacks keybindCallback = new KeybindCallbacks(mc);
        Configs.Hotkeys.KEY_OPEN_CONFIG_GUI.getKeybind().setCallback(keybindCallback);
        Configs.Hotkeys.RESET_SCROLL.getKeybind().setCallback(keybindCallback);
        Configs.Settings.HYBRID_RENDER.setValueChangeCallback(changeCallback);
        Configs.Settings.COMPRESS.setValueChangeCallback(changeCallback);
    }

    public static class ChangeCallback implements IValueChangeCallback<ConfigBoolean> {
        @Override
        public void onValueChanged(ConfigBoolean config) {
            Screen gui = mc_.currentScreen;
            if (Configs.Settings.COMPRESS.getBooleanValue() && Configs.Settings.HYBRID_RENDER.getBooleanValue()) {
                if (config == Configs.Settings.COMPRESS) {
                    Configs.Settings.HYBRID_RENDER.setBooleanValue(false);
                    if (gui instanceof GuiConfigs) {
                        ((GuiConfigs) gui).initGui();
                    }
                } else if (config == Configs.Settings.HYBRID_RENDER) {
                    Configs.Settings.COMPRESS.setBooleanValue(false);
                    if (gui instanceof GuiConfigs) {
                        ((GuiConfigs) gui).initGui();
                    }
                }
            }
        }
    }
}

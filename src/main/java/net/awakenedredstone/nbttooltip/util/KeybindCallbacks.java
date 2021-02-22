package net.awakenedredstone.nbttooltip.util;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.awakenedredstone.nbttooltip.NBTTooltip;
import net.awakenedredstone.nbttooltip.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.awakenedredstone.nbttooltip.gui.GuiConfigs;

public class KeybindCallbacks implements IHotkeyCallback {

    private final MinecraftClient mc;

    public KeybindCallbacks(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public boolean onKeyAction(KeyAction keyAction, IKeybind key) {
        if (mc.player == null || mc.world == null) {
            return false;
        }

        if (key == Configs.Hotkeys.KEY_OPEN_CONFIG_GUI.getKeybind()) {
            GuiBase.openGui(new GuiConfigs());
            return true;
        }

        if (key == Configs.Hotkeys.RESET_SCROLL.getKeybind()) {

            NBTTooltip.line_scrolled = 0;
            return true;
        }
        return false;
    }
}

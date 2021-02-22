package net.awakenedredstone.nbttooltip;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.awakenedredstone.nbttooltip.gui.GuiConfigs;

public class ModMenuSupport implements ModMenuApi {
    //Because I don't want to restart game all time
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        };
    }
}

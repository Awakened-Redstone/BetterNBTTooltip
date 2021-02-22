package net.awakenedredstone.nbttooltip.util;

import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.util.KeyCodes;
import net.awakenedredstone.nbttooltip.NBTTooltip;
import net.awakenedredstone.nbttooltip.config.InputHandler;

public class ScrollUtil implements IMouseInputHandler {

    private static final ScrollUtil INSTANCE = new ScrollUtil();

    public static ScrollUtil getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        return this.handleInput(KeyCodes.KEY_NONE, false, amount);
    }

    private boolean handleInput(int keyCode, boolean keyState, double dWheel) {
        boolean cancel = false;
        if (NBTTooltip.line_scrolled == 0 && -dWheel < 0) {
            cancel = true;
        }
        else {
            if (NBTTooltip.line_scrolled + -dWheel < 0) NBTTooltip.line_scrolled = 0;
            else NBTTooltip.line_scrolled += -dWheel;
        }


        return cancel;
    }
}

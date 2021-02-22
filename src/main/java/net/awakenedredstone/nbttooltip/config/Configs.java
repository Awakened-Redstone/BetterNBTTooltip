package net.awakenedredstone.nbttooltip.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = "nbttooltip.json";

    public static class Settings {
        public static final ConfigInteger MAX_LINES_SHOWN = new ConfigInteger("maxLinesShoun", 10, 1, 100, "");
        public static final ConfigInteger MAX_WIDTH = new ConfigInteger("maxWidth", 30, 1, 100, "");
        public static final ConfigInteger TICKS_BEFORE_SCROLL = new ConfigInteger("ticksBeforeScroll", 20, 0, 200, "");
        public static final ConfigInteger SCROLL_ACCELERATION_FACTOR = new ConfigInteger("scrollAccelerationFactor", 4, 1, 20, "");
        public static final ConfigBoolean SHOW_SEPARATOR = new ConfigBoolean("showSeparator", false, "");
        //public static final ConfigBoolean REQUIRES_F3 = new ConfigBoolean("requiresf3", false, "");
        public static final ConfigBoolean SHOW_DELIMITERS = new ConfigBoolean("showDelimiters", false, "");
        public static final ConfigBoolean COMPRESS = new ConfigBoolean("compress", false, "");
        public static final ConfigBoolean HYBRID_RENDER = new ConfigBoolean("hybridRender", false, "");
        //public static final ConfigBoolean CTRL_SUPPRESSES_RESET = new ConfigBoolean("ctrlSuppressesRest", false, "");
        public static final ConfigBoolean ALWAYS_SHOW = new ConfigBoolean("alwaysShow", false, "");


        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                MAX_LINES_SHOWN,
                MAX_WIDTH,
                TICKS_BEFORE_SCROLL,
                SCROLL_ACCELERATION_FACTOR,
                SHOW_SEPARATOR,
                //REQUIRES_F3,
                SHOW_DELIMITERS,
                COMPRESS,
                HYBRID_RENDER,
                //CTRL_SUPPRESSES_RESET,
                ALWAYS_SHOW
        );
    }

    public static class Hotkeys {
        private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);

        public static final ConfigHotkey SHOW = new ConfigHotkey("showNBT", "A", GUI_RELAXED, "");
        public static final ConfigHotkey RESET_SCROLL = new ConfigHotkey("resetScroll", "LEFT_ALT", GUI_RELAXED, "");
        public static final ConfigHotkey PAUSE_SCROLL = new ConfigHotkey("pauseScroll", "LEFT_SHIFT", GUI_RELAXED, "");
        public static final ConfigHotkey COPY = new ConfigHotkey("copy", "DOWN", GUI_RELAXED, "");
        public static final ConfigHotkey SHOW_ONLY_NBT = new ConfigHotkey("showOnlyNBT", "LEFT_CONTROL", GUI_RELAXED, "");
        public static final ConfigHotkey SIMPLE = new ConfigHotkey("simple", "LEFT_SHIFT", GUI_RELAXED, "");
        public static final ConfigHotkey BREAK_LINES = new ConfigHotkey("breakLines", "LEFT_SHIFT", GUI_RELAXED, "");
        public static final ConfigHotkey KEY_OPEN_CONFIG_GUI = new ConfigHotkey("openConfigGui", "N,C", "Open the in-game config GUI");


        public static final ImmutableList<ConfigHotkey> HOTKEYS = ImmutableList.of(
                SHOW,
                RESET_SCROLL,
                PAUSE_SCROLL,
                COPY,
                SHOW_ONLY_NBT,
                SIMPLE,
                BREAK_LINES,
                KEY_OPEN_CONFIG_GUI
        );
    }

    public static class Lists {
        public static final ConfigStringList BREAK_BLACKLIST = new ConfigStringList("breakBlacklist", ImmutableList.of(), "");

        public static final ImmutableList<IConfigBase> LISTS = ImmutableList.of(
                BREAK_BLACKLIST
        );
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Settings", Settings.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.HOTKEYS);
                ConfigUtils.readConfigBase(root, "Lists", Lists.LISTS);
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Settings", Settings.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.HOTKEYS);
            ConfigUtils.writeConfigBase(root, "Lists", Lists.LISTS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }
}

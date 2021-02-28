package net.awakenedredstone.nbttooltip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.dy.masa.malilib.event.InitializationHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.SystemToast.Type;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.awakenedredstone.nbttooltip.config.Configs;

public class NBTTooltip implements ClientModInitializer {

    public static final Logger logger = LogManager.getLogger("nbttooltip");

    //public static int ticks = 0;
    public static int line_scrolled = 0;

    public static final String FORMAT = Formatting.ITALIC.toString() + Formatting.DARK_GRAY;

    public static KeyBinding COPY_TO_CLIPBOARD = new KeyBinding("key.nbttooltip.right", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_DOWN, "key.category.nbttooltip");
    public static boolean flipflop_key_copy = false;

    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        ClientTickEvents.END_CLIENT_TICK.register(NBTTooltip::clientTick);
        ItemTooltipCallback.EVENT.register(NBTTooltip::onInjectTooltip);
        //KeyBindingHelper.registerKeyBinding(COPY_TO_CLIPBOARD);
    }

    public static void clientTick(MinecraftClient mc) {
        if (line_scrolled < 0) line_scrolled = 0;
        /*if (!Configs.Hotkeys.PAUSE_SCROLL.getKeybind().isKeybindHeld()) {
            NBTTooltip.ticks++;
            int factor = 1;
            if (Configs.Hotkeys.ACCELERATE_SCROLL.getKeybind().isKeybindHeld()) {
                factor = Configs.Settings.SCROLL_ACCELERATION_FACTOR.getIntegerValue();
            }
            if (NBTTooltip.ticks >= Configs.Settings.TICKS_BEFORE_SCROLL.getIntegerValue() / factor) {
                NBTTooltip.ticks = 0;
                NBTTooltip.line_scrolled++;
            }
        }*/
    }

    public static ArrayList<Text> transformTtip(ArrayList<Text> ttip, int lines) {
        ArrayList<Text> newttip = new ArrayList<Text>(lines);
        if (Configs.Settings.SHOW_SEPARATOR.getBooleanValue()) {
            newttip.add(new LiteralText("- NBTTooltip -"));
        }
        if (ttip.size() > lines) {
            if (lines + line_scrolled > ttip.size()) line_scrolled = ttip.size() - 1;
            for (int i = 0; i < lines; i++) {
                newttip.add(ttip.get(i + line_scrolled));
            }
        } else {
            line_scrolled = 0;
            newttip.addAll(ttip);
        }
        return newttip;
    }

    public static void unwrapTag(List<Text> tooltip, Tag base, String pad, String tagName, String padIncrement, boolean splitLongStrings) {
        if (base instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag) base;
            tag.getKeys().forEach(s -> {
                boolean nested = (tag.get(s) instanceof AbstractListTag) || (tag.get(s) instanceof CompoundTag);
                if (nested) {
                    tooltip.add(new LiteralText(pad + s + ": {"));
                    unwrapTag(tooltip, tag.get(s), pad + padIncrement, s, padIncrement, splitLongStrings);
                    tooltip.add(new LiteralText(pad + "}"));
                } else {
                    addValueToTooltip(tooltip, tag.get(s), s, pad, splitLongStrings);
                }
            });
        } else if (base instanceof AbstractListTag) {
            AbstractListTag<?> tag = (AbstractListTag<?>) base;
            int index = 0;
            Iterator<? extends Tag> iter = tag.iterator();
            while (iter.hasNext()) {
                Tag nbtnext = iter.next();
                if (nbtnext instanceof AbstractListTag || nbtnext instanceof CompoundTag) {
                    tooltip.add(new LiteralText(pad + "[" + index + "]: {"));
                    unwrapTag(tooltip, nbtnext, pad + padIncrement, "", padIncrement, splitLongStrings);
                    tooltip.add(new LiteralText(pad + "}"));
                } else {
                    addValueToTooltip(tooltip, nbtnext, "[" + index + "]", pad, splitLongStrings);
                }
                index++;
            }
        } else {
            addValueToTooltip(tooltip, base, tagName, pad, splitLongStrings);
        }
    }

    private static void addValueToTooltip(List<Text> tooltip, Tag nbt, String name, String pad, boolean splitLongStrings) {
        int limit = Configs.Settings.MAX_WIDTH.getIntegerValue();
        String toBeAdded = nbt.toString();
        if (!splitLongStrings || toBeAdded.length() < limit) {
            tooltip.add(new LiteralText(pad + name + ": " + nbt.toString()));
        } else {
            int added = 0;
            tooltip.add(new LiteralText(pad + name + ":"));
            while (added < toBeAdded.length()) {
                int nextChunk = Math.min(limit, toBeAdded.length() - added);
                StringBuilder sb = new StringBuilder(Formatting.AQUA.toString())
                        .append("|")
                        .append(Formatting.RESET.toString())
                        .append(pad)
                        .append("   ")
                        .append(toBeAdded.substring(added, added + nextChunk));
                tooltip.add(new LiteralText(sb.toString()));
                added += nextChunk;
            }
        }

    }

    private static void addValueToTooltip_(List<Text> tooltip, Tag nbt, String name, String pad, boolean splitLongStrings) {
        int limit = Configs.Settings.MAX_WIDTH.getIntegerValue();
        String toBeAdded = nbt.toString();
        if (!splitLongStrings || toBeAdded.length() < limit) {
            tooltip.add(new LiteralText(pad + name + nbt.toString()));
        } else {
            int added = 0;
            while (added < toBeAdded.length()) {
                int nextChunk = Math.min(limit, toBeAdded.length() - added);
                int stretch = 0;
                if (!Configs.Lists.BREAK_BLACKLIST.getStrings().isEmpty()) {
                    for (String text : Configs.Lists.BREAK_BLACKLIST.getStrings()) {
                        if (text.length() > 0) {
                            stretch = text.equals(toBeAdded.substring(added + nextChunk + 1, added + nextChunk + text.length())) ? text.length() : stretch;
                        }
                    }
                }
                StringBuilder sb = new StringBuilder(Formatting.AQUA.toString())
                        //.append("|")
                        .append(Formatting.RESET.toString())
                        .append(pad)
                        //.append("   ")
                        .append(toBeAdded.substring(added, added + nextChunk));
                tooltip.add(new LiteralText(sb.toString()));
                added += nextChunk;
            }
        }

    }

    public static void onInjectTooltip(ItemStack stack, TooltipContext context, List<Text> list) {
        handleClipboardCopy(stack, list);
        //logger.info(Configs.Hotkeys.SHOW.getKeybind().isKeybindHeld());
        //if (!Configs.Main.REQUIRES_F3.getBooleanValue() || context.isAdvanced()) {
        if (Configs.Settings.ALWAYS_SHOW.getBooleanValue() || (!Configs.Settings.ALWAYS_SHOW.getBooleanValue() && Configs.Hotkeys.SHOW.getKeybind().isKeybindHeld())) {
            int lines = Configs.Settings.MAX_LINES_SHOWN.getIntegerValue();
            if (Configs.Hotkeys.SHOW_ONLY_NBT.getKeybind().isKeybindHeld()) {
                lines += list.size();
                list.clear();
            } else {
                list.add(new LiteralText(""));
            }
            CompoundTag tag = stack.getTag();
            ArrayList<Text> ttip = new ArrayList<Text>(lines);
            if (tag != null) {
                if (Configs.Settings.SHOW_DELIMITERS.getBooleanValue()) {
                    ttip.add(new LiteralText(Formatting.DARK_PURPLE + " - nbt start -"));
                }
                if (Configs.Settings.COMPRESS.getBooleanValue() && !Configs.Settings.HYBRID_RENDER.getBooleanValue()) {
                    ttip.add(new LiteralText(FORMAT + tag.toString()));
                } else if (!Configs.Settings.COMPRESS.getBooleanValue() && Configs.Settings.HYBRID_RENDER.getBooleanValue() && Configs.Hotkeys.BREAK_LINES.getKeybind().isKeybindHeld()) {
                    addValueToTooltip_(ttip, tag, "", FORMAT, true);
                } else if (!Configs.Settings.COMPRESS.getBooleanValue() && Configs.Settings.HYBRID_RENDER.getBooleanValue() && !Configs.Hotkeys.BREAK_LINES.getKeybind().isKeybindHeld()) {
                    addValueToTooltip_(ttip, tag, "", FORMAT, false);
                } else if (Configs.Settings.COMPRESS.getBooleanValue() && Configs.Settings.HYBRID_RENDER.getBooleanValue()) {
                    ttip.add(new TranslatableText("Invalid config"));
                } else {
                    NBTTooltip.unwrapTag(ttip, tag, FORMAT, "", Configs.Settings.COMPRESS.getBooleanValue() ? "" : "  ", true);
                }
                if (Configs.Settings.SHOW_DELIMITERS.getBooleanValue()) {
                    ttip.add(new LiteralText(Formatting.DARK_PURPLE + " - nbt end -"));
                }
                ttip = NBTTooltip.transformTtip(ttip, lines);

                list.addAll(ttip);
            } else {
                list.add(new LiteralText(FORMAT + "No NBT tag"));
            }
        }
    }

    private static void handleClipboardCopy(ItemStack stack, List<Text> list) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.currentScreen != null) {
            if (Configs.Hotkeys.COPY.getKeybind().isKeybindHeld()) {
                if (!flipflop_key_copy) {
                    flipflop_key_copy = true;
                    copyToClipboard(stack, list, mc);
                }
            } else {
                flipflop_key_copy = false;
            }

        }
    }

    private static void copyToClipboard(ItemStack stack, List<Text> list, MinecraftClient mc) {
        StringBuilder sb = new StringBuilder();
        String name = I18n.translate(stack.getTranslationKey());
        if (!Configs.Hotkeys.SIMPLE.getKeybind().isKeybindHeld()) {
            sb.append("Item ID: ");
            sb.append(Registry.ITEM.getKey(stack.getItem()).map(rk -> rk.getValue().toString()).orElse("ID NOT FOUND IN REGISTRY"));
            sb.append("\nItem name:");
            sb.append(name);
            sb.append("\nAmount: ");
            sb.append(stack.getCount());
            sb.append("\n");
            if (stack.getTag() == null) {
                sb.append("No Item NBT attached to the stack\n");
            } else {
                ArrayList<Text> copy = new ArrayList<>(list);
                sb.append("\n -- NBT Tag --\n\n{\n");
                copy.removeIf(t -> t.asString().trim().equals(""));
                copy.remove(0);
                unwrapTag(copy, stack.getTag(), "\t", "Item NBT", "\t", false);
                copy.forEach(t -> {
                    sb.append(t.asString());
                    sb.append("\n");
                });
                sb.append("}\n");
            }
            try {
                mc.keyboard.setClipboard(sb.toString());
                mc.getToastManager().add(new SystemToast(Type.TUTORIAL_HINT, new TranslatableText("nbttooltip.copied_to_clipboard"), new TranslatableText("nbttooltip.object_details", name)));
            } catch (Exception e) {
                mc.getToastManager().add(new SystemToast(Type.TUTORIAL_HINT, new TranslatableText("nbttooltip.copy_failed"), new LiteralText(e.getMessage())));
                e.printStackTrace();
            }
        } else {
            sb.append(Registry.ITEM.getKey(stack.getItem()).map(rk -> rk.getValue().toString()).orElse("invalid_item"));
            if (stack.getTag() == null) {
                sb.append("{}");
            } else {
                sb.append(stack.getTag().toString());
            }
            sb.append(" ");
            sb.append(stack.getCount());
            try {
                mc.keyboard.setClipboard(sb.toString());
                mc.getToastManager().add(new SystemToast(Type.TUTORIAL_HINT, new TranslatableText("nbttooltip.copied_to_clipboard"), new TranslatableText("nbttooltip.object_details", name)));
            } catch (Exception e) {
                mc.getToastManager().add(new SystemToast(Type.TUTORIAL_HINT, new TranslatableText("nbttooltip.copy_failed"), new LiteralText(e.getMessage())));
                e.printStackTrace();
            }
        }
    }

}

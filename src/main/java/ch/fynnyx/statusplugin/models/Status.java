package ch.fynnyx.statusplugin.models;

import net.md_5.bungee.api.ChatColor;

public class Status {

    private final String key; // config key, e.g. "afk"
    private final String name; // e.g. "AFK"
    private final ChatColor color; // e.g. ChatColor.GRAY

    public Status(String key, String name, ChatColor color) {
        this.key = key;
        this.name = name;
        this.color = color;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getColoredPrefix() {
        return color + name;
    }

    @Override
    public String toString() {
        return "Status{name='" + name + "', color=" + color + "}";
    }

}

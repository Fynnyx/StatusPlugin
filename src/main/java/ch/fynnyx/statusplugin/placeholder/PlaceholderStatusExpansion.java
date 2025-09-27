package ch.fynnyx.statusplugin.placeholder;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderStatusExpansion extends PlaceholderExpansion {

    FileConfiguration config;

    public PlaceholderStatusExpansion(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Fynnyx";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "status";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.equals("status")) {
            String placeholderString = this.config.getString("placeholders.status.placeholderString");
            String playerstatus = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
            String prefix = config.getString("statuses." + playerstatus + ".prefix");
            String color = config.getString("statuses." + playerstatus + ".color");
            String statusString = "§" + color + prefix + "§r";
            if (!config.getBoolean("placeholders.status.returnWithColorFormatting")) {
                statusString = ChatColor.stripColor(statusString);
            }
            String status = placeholderString.replace("%status%", statusString);
            return status != null ? status : "";
        }
        return null;
    }

}

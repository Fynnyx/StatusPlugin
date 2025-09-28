package ch.fynnyx.statusplugin.placeholder;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;

public class PlaceholderStatusExpansion extends PlaceholderExpansion {

    PlayerStatusManager playerStatusManager;
    FileConfiguration config;

    public PlaceholderStatusExpansion(FileConfiguration config, PlayerStatusManager playerStatusManager) {
        this.config = config;
        this.playerStatusManager = playerStatusManager;
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
            String placeholderString = config.getString("placeholders.status.placeholderString");

            placeholderString = placeholderString.replace("%status%", playerStatusManager.getPlayerStatus(player).getColoredName());

            if (!config.getBoolean("placeholders.status.returnWithColorFormatting")) {
                placeholderString = ChatColor.stripColor(placeholderString);
            }

            return placeholderString;
        }
        return null;
    }

}

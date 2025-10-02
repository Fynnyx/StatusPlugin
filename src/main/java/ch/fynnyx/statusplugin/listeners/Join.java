package ch.fynnyx.statusplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;

public class Join implements Listener {

    private final PlayerStatusManager playerStatusManager;
    private final FileConfiguration config;

    public Join(FileConfiguration config, PlayerStatusManager playerStatusManager) {
        this.config = config;
        this.playerStatusManager = playerStatusManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Always update their tablist display name
        Status status = playerStatusManager.getPlayerStatus(player);
        playerStatusManager.updateDisplayName(player, status.getKey());

        // Show join message if enabled
        if (config.getBoolean("join-leave.enabled", true)) {
            String format = config.getString("join-leave.join-message-format",
                    "%status% &e%username% joined the game.");

            format = format.replace("%status%", status.getColoredName())
                    .replace("%username%", player.getName());

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
            }

            format = ChatColor.translateAlternateColorCodes('&', format);
            event.setJoinMessage(format);
        }
    }
}

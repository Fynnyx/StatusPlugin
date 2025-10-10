package ch.fynnyx.statusplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;

public class Quit implements Listener {

    private final PlayerStatusManager playerStatusManager;
    private final FileConfiguration config;

    public Quit(FileConfiguration config, PlayerStatusManager playerStatusManager) {
        this.config = config;
        this.playerStatusManager = playerStatusManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Resolve player status (falls back to default if unset)
        Status status = playerStatusManager.getPlayerStatus(player);

        if (config.getBoolean("join-leave.enabled", true)) {
            String format = config.getString("join-leave.leave-message-format",
                    "&e%status% &c%username% left the game.");

            format = format.replace("%status%", status != null ? status.getColoredName() : "")
                    .replace("%username%", player.getName());

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
            }

            format = ChatColor.translateAlternateColorCodes('&', format);
            event.setQuitMessage(format);
        }
    }
}

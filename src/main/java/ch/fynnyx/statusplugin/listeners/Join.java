package ch.fynnyx.statusplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;
import net.luckperms.api.LuckPerms;

public class Join implements Listener {

    private final PlayerStatusManager playerStatusManager;
    private final LuckPerms luckPerms;
    private final FileConfiguration config = Statusplugin.getPlugin(Statusplugin.class).getConfig();

    public Join(PlayerStatusManager playerStatusManager, LuckPerms luckPerms) {
        this.playerStatusManager = playerStatusManager;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Always update their tablist display name
        Status status = playerStatusManager.getPlayerStatus(player);
        playerStatusManager.updateDisplayName(player, status.getKey());

        // Show join message if enabled
        if (config.getBoolean("join-leave.enabled", true)) {
            String format = config.getString("join-leave.join-message-format", "%status% &e%username% joined the game.");

            format = format.replace("%status%", status.getColoredName())
                           .replace("%username%", player.getName());

            if (luckPerms != null) {
                String lpPrefix = luckPerms.getPlayerAdapter(Player.class)
                                           .getUser(player)
                                           .getCachedData()
                                           .getMetaData()
                                           .getPrefix();
                format = format.replace("%luckperms%", lpPrefix != null ? lpPrefix : "");
            } else {
                format = format.replace("%luckperms%", "");
            }

            format = ChatColor.translateAlternateColorCodes('&', format);
            event.setJoinMessage(format);
        }
    }
}

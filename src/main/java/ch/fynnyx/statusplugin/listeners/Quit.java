package ch.fynnyx.statusplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;
import net.luckperms.api.LuckPerms;

public class Quit implements Listener {

    private final PlayerStatusManager playerStatusManager;
    private final LuckPerms luckPerms;
    private final FileConfiguration config = Statusplugin.getPlugin(Statusplugin.class).getConfig();

    public Quit(PlayerStatusManager playerStatusManager, LuckPerms luckPerms) {
        this.playerStatusManager = playerStatusManager;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Resolve player status (falls back to default if unset)
        Status status = playerStatusManager.getPlayerStatus(player);

        if (config.getBoolean("show-join-leave-message", false)) {
            String format = config.getString("quit-message-format", "&e%status% &c%username% left the game.");

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
            event.setQuitMessage(format);
        }
    }
}

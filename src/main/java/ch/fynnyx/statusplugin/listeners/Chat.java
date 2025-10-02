package ch.fynnyx.statusplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;
import net.luckperms.api.LuckPerms;

public class Chat implements Listener {

    private final PlayerStatusManager playerStatusManager;
    private final LuckPerms luckPerms;
    private final FileConfiguration config = Statusplugin.getPlugin(Statusplugin.class).getConfig();

    public Chat(PlayerStatusManager playerStatusManager, LuckPerms luckPerms) {
        this.playerStatusManager = playerStatusManager;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!config.getBoolean("chat.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();

        // Get player status (with default fallback)
        Status status = playerStatusManager.getPlayerStatus(player);

        String format = config.getString(
                "chat.format",
                "&7[%status%] &f%username%: %message%"
        );

        format = format.replace("%status%", status.getColoredName())
                       .replace("%username%", player.getName())
                       .replace("%message%", message);

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
        event.setFormat(format);
    }
}

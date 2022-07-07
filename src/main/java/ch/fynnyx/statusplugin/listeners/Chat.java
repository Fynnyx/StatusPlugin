package ch.fynnyx.statusplugin.listeners;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
    FileConfiguration config;
    public Chat(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        try {
            if (config.getBoolean("show-in-chat")) {
                String format = config.getString("chat-format");
                String status = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
                String color = config.getString("statuses." + status + ".color");
                String prefix = config.getString("statuses." + status + ".prefix");
                if ((color == null || prefix == null)) {
                    color = "";
                    prefix = "";
                }
                format = format.replace("%status%", "§" + color + prefix)
                                .replace("%username%", player.getName())
                                .replace("%message%", message)
                                .replace("%luckperms%", "LUCKPERMS");
                format = ChatColor.translateAlternateColorCodes('&', format);
                event.setFormat(format);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}

package ch.fynnyx.statusplugin.listeners;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
    FileConfiguration config;
    LuckPerms luckPerms;
    public Chat(FileConfiguration config, LuckPerms luckPerms) {
        this.config = config;
        this.luckPerms = luckPerms;
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
                User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                if ((color == null || prefix == null)) {
                    color = "";
                    prefix = "";
                }
                format = format.replace("%status%", "§" + color + prefix)
                                .replace("%username%", player.getName())
                                .replace("%message%", message);
                if (!(luckPerms == null || luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix() == null)) {
                    format = format.replace("%luckperms%", luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix());
                } else {
                    format = format.replace("%luckperms%", "LUCKPERMS");
                }
                format = ChatColor.translateAlternateColorCodes('&', format);
                event.setFormat(format);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}

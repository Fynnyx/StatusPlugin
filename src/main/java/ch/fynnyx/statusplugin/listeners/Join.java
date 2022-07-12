package ch.fynnyx.statusplugin.listeners;

import ch.fynnyx.statusplugin.commands.StatusCommand;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    FileConfiguration config;
    LuckPerms luckPerms;

    public Join(FileConfiguration config, LuckPerms luckPerms) {
        this.config = config;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            if(config.getBoolean("default.use-default-on-join")) {
                StatusPlayerConfigFile.getConfig().set("statuses." + player.getUniqueId(), config.getString("default.default-status"));
                StatusPlayerConfigFile.saveConfig();
            }
            StatusCommand.setStatus(player);
            if(config.getBoolean("show-join-leave-message")) {
                String status = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
                String color = config.getString("statuses." + status + ".color");
                String prefix = config.getString("statuses." + status + ".prefix");
                if ((color == null || prefix == null)) {
                    color = "";
                    prefix = "";
                }
                String format = config.getString("join-message-format");

                format = format.replace("%status%", "§" + color + prefix)
                        .replace("%username%", player.getName());

                if (!(luckPerms == null || luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix() == null)) {
                    format = format.replace("%luckperms%", luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix());
                } else {
                    format = format.replace("%luckperms%", "LUCKPERMS");
                }
                format = ChatColor.translateAlternateColorCodes('&', format);
                event.setJoinMessage(format);
            }
        } catch (NullPointerException e) {
            System.out.println(e.getStackTrace());
        }
    }
}

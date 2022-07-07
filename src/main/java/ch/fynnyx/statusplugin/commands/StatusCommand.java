package ch.fynnyx.statusplugin.commands;

import ch.fynnyx.statusplugin.Statusplugin;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class StatusCommand implements CommandExecutor {
    static FileConfiguration config;
    public StatusCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {

            String status = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
            String color = config.getString("statuses." + status + ".color");
            String prefix = config.getString("statuses." + status + ".prefix");
            sender.sendMessage(ChatColor.GOLD + "Your status is: §" + color + prefix);
            return true;
        }

        String color = config.getString("statuses." + args[0] + ".color");
        String prefix = config.getString("statuses." + args[0] + ".prefix");
        if (color == null || prefix == null) {
            sender.sendMessage(ChatColor.RED + "Status not found!");
            return true;
        }
        savePlayerStatus(player, args[0]);
        setStatus(player);

        player.sendMessage(ChatColor.GOLD + "Your status has been set to §" + color + prefix);
        System.out.println(ChatColor.GOLD + "Player " + player.getName() + " has set their status to §" + color + prefix);
        return true;
    }

    public static void setStatus(Player player) {
        try {
            if (player == null) {
                player.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            }
            String status = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
            String color = config.getString("statuses." + status + ".color");
            String prefix = config.getString("statuses." + status + ".prefix");
            if (color == null || prefix == null) {
                status = config.getString("default.default-status");
                color = config.getString("statuses." + status + ".color");
                prefix = config.getString("statuses." + status + ".prefix");
                if (status.isEmpty()) {
                    color = "";
                    prefix = "";
                } else if (color == null || prefix == null) {
                    color = "";
                    prefix = "";
                }
                savePlayerStatus(player, status);
            }
            if (config.getBoolean("show-in-tablist")) {
                player.setPlayerListName(ChatColor.GRAY + "[§" + color + prefix + ChatColor.GRAY + "] " + ChatColor.WHITE + player.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void savePlayerStatus(Player player, String status) {
        StatusPlayerConfigFile.getConfig().set("statuses." + player.getUniqueId(), status);
        StatusPlayerConfigFile.saveConfig();
    }
}

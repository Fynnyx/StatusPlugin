package ch.fynnyx.statusplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.luckperms.api.LuckPerms;

public class StatusCommand implements CommandExecutor {
    static FileConfiguration config;
    static LuckPerms luckPerms;
    public StatusCommand(FileConfiguration config, LuckPerms luckPerms) {
        StatusCommand.config = config;
        StatusCommand.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length == 2) {
//           Check for permission status.setother
            if (!(sender.hasPermission("status.setother") || sender.isOp())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to set other players status!");
                return true;
            }
            // Check if the arg is a player
            Player target = sender.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[1] + ChatColor.RED + " not found!");
                return true;
            }
            String color = config.getString("statuses." + args[0] + ".color");
            String prefix = config.getString("statuses." + args[0] + ".prefix");
            if (color == null || prefix == null) {
                sender.sendMessage(ChatColor.RED + "Status not found!");
                return true;
            }
            savePlayerStatus(target, args[0]);
            setStatus(target);
            sender.sendMessage(ChatColor.GOLD + "The status of " + target.getName() + " has set to: §" + color + prefix);
            return true;
        }
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
        if (args.length == 1) {
            String color = config.getString("statuses." + args[0] + ".color");
            String prefix = config.getString("statuses." + args[0] + ".prefix");
            if (color == null || prefix == null) {
                sender.sendMessage(ChatColor.RED + "Status not found!");
                return true;
            }
            savePlayerStatus(player, args[0]);
            setStatus(player);

            player.sendMessage(ChatColor.GOLD + "Your status has been set to §" + color + prefix);
            Bukkit.getLogger().info("Player " + player.getName() + " has set their status to §" + color + prefix);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "To many arguments!");
        return false;
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
                if (status.isEmpty() || color == null || prefix == null) {
                    color = "";
                    prefix = "";
                }
                savePlayerStatus(player, status);
            }
            if (config.getBoolean("show-in-tablist")) {
                String format = config.getString("tablist-format");
                format = format.replace("%status%", "§" + color + prefix)
                        .replace("%username%", player.getName());
                if (!(luckPerms == null || luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix() == null)) {
                    format = format.replace("%luckperms%", luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getPrefix());
                } else {
                    format = format.replace("%luckperms%", "LUCKPERMS");
                }
                format = ChatColor.translateAlternateColorCodes('&', format);
                player.setPlayerListName(format);
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

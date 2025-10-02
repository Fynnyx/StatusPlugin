package ch.fynnyx.statusplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.models.Status;

public class StatusCommand implements CommandExecutor {

    private final PlayerStatusManager playerStatusManager;

    public StatusCommand(PlayerStatusManager playerStatusManager) {
        this.playerStatusManager = playerStatusManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            String key = playerStatusManager.getPlayerStatusKey(player);
            sender.sendMessage(ChatColor.GOLD + "Your status is: " + key);
            return true;
        }

        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            Status status = playerStatusManager.setPlayerStatus(player, args[0]);

            if (!playerStatusManager.playerHasStatusPermission(player, args[0]) && !player.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use the status " + status.getColoredName());
                return true;
            }
            player.sendMessage(ChatColor.GOLD + "Your status has been set to " + status.getColoredName());
            return true;
        }

        if (args.length == 2) {
            if (!(sender.hasPermission("status.setother") || sender.isOp())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            Player target = sender.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            if (!playerStatusManager.playerHasStatusPermission((Player) sender, args[0]) && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use the status " + args[0]);
                return true;
            }
            playerStatusManager.setPlayerStatus(target, args[0]);
            sender.sendMessage(ChatColor.GOLD + "Set " + target.getName() + "'s status to " + args[0]);
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage: /status [status] [player]");
        return false;
    }
}

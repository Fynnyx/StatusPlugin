package ch.fynnyx.statusplugin.commands;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ch.fynnyx.statusplugin.manager.StatusManager;
import ch.fynnyx.statusplugin.models.Status;

public class StatusesCommand implements CommandExecutor {
    private final StatusManager statusManager;

    public StatusesCommand(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If sender has the status.list permission set to false, deny access
        if (!(sender.hasPermission("status.list") || sender.isOp())) {
            sender.sendMessage("§cYou don't have permission to list the statuses!");
            return true;
        }

        Collection<Status> statuses = statusManager.getStatuses();
        String text = ChatColor.GOLD + "Available statuses:\n";
        for (Status status : statuses) {
            text += ChatColor.DARK_GRAY + "- " + status.getColoredName() + "\n";
        }
        sender.sendMessage(text);
        return true;
    }
}

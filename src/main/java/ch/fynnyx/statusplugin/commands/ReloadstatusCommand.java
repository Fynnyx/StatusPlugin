package ch.fynnyx.statusplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import ch.fynnyx.statusplugin.Statusplugin;

public class ReloadstatusCommand implements CommandExecutor {

    private final Statusplugin plugin;

    public ReloadstatusCommand(Statusplugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label,
            @NonNull String[] args) {
        if (!(sender.hasPermission("status.reload") || sender.isOp())) {
            sender.sendMessage("§cYou don't have permission to reload the config!");
            return true;
        }

        // Reload the config
        plugin.reloadPlugin();
        sender.sendMessage("§aStatusplugin configuration reloaded!");

        return true;
    }

}

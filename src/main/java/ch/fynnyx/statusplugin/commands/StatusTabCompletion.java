package ch.fynnyx.statusplugin.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import ch.fynnyx.statusplugin.manager.StatusManager;
import ch.fynnyx.statusplugin.models.Status;

public class StatusTabCompletion implements TabCompleter {
    
    StatusManager statusManager;

    public StatusTabCompletion(StatusManager statusManager) {
        this.statusManager = statusManager;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> statuses = new ArrayList<>();
            Collection<Status> statusCollection = statusManager.getStatuses();
            for (Status status : statusCollection) {
                if (status.getKey().toLowerCase().startsWith(args[0].toLowerCase())) {
                    statuses.add(status.getKey());
                }
            }
            // Sort the statuses alphabetically
            statuses.sort(String.CASE_INSENSITIVE_ORDER);
            return statuses;
        }
        return null;
    }
}

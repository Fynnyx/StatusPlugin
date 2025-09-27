package ch.fynnyx.statusplugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

public class StatusTabCompletion implements TabCompleter {
    FileConfiguration config;

    public StatusTabCompletion(FileConfiguration config) {
        this.config = config;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> statuses = new ArrayList<>();
            for (String status : config.getConfigurationSection("statuses").getKeys(false)) {
                if (status.toLowerCase().startsWith(args[0].toLowerCase())) {
                    statuses.add(status);
                }
            }
            return statuses;
        }
        return null;
    }
}

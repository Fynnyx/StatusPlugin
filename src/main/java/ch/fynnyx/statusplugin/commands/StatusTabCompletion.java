package ch.fynnyx.statusplugin.commands;

import ch.fynnyx.statusplugin.Statusplugin;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class StatusTabCompletion implements TabCompleter {
    FileConfiguration config;

    public StatusTabCompletion(FileConfiguration config) {
        this.config = config;
    }
    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> statuses = new ArrayList<>();
            for (String status : config.getConfigurationSection("statuses").getKeys(false)) {
                statuses.add(status);
            }
            return statuses;
        }
        return null;
    }
}

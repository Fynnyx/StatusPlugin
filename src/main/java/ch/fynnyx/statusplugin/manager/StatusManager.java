package ch.fynnyx.statusplugin.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import ch.fynnyx.statusplugin.models.Status;
import net.md_5.bungee.api.ChatColor;

public class StatusManager {

    private final Map<String, Status> statuses = new HashMap<>();
    private String defaultStatusKey;

    /**
     * Loads statuses from the config file.
     */
    public void loadStatuses(FileConfiguration config) {
        statuses.clear();

        // Load default from config
        this.defaultStatusKey = config.getString("default.default-status");

        ConfigurationSection section = config.getConfigurationSection("statuses");
        if (section == null) {
            Bukkit.getLogger().warning("No 'statuses' section found in config.yml");
            return;
        }

        for (String key : section.getKeys(false)) {
            String prefix = section.getString(key + ".prefix");
            String colorCode = section.getString(key + ".color");

            if (prefix == null || colorCode == null || colorCode.isEmpty()) {
                Bukkit.getLogger().warning("Skipping invalid status: " + key);
                continue;
            }

            ChatColor color = ChatColor.getByChar(colorCode.charAt(0));
            if (color == null) {
                Bukkit.getLogger().warning("Invalid color code '" + colorCode + "' for status: " + key);
                continue;
            }

            statuses.put(key.toLowerCase(), new Status(key, prefix, color));
        }
    }

    /**
     * Returns an unmodifiable collection of all loaded statuses.
     */
    public Collection<Status> getStatuses() {
        return Collections.unmodifiableCollection(statuses.values());
    }

    /**
     * Finds a status by its config key (case-insensitive).
     */
    public Optional<Status> getByKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(statuses.get(key.toLowerCase()));
    }

    /**
     * Returns the default status defined in config.
     */
    public Optional<Status> getDefaultStatus() {
        if (defaultStatusKey == null) {
            return Optional.empty();
        }
        return getByKey(defaultStatusKey);
    }
}

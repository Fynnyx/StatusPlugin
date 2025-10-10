package ch.fynnyx.statusplugin.manager;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import ch.fynnyx.statusplugin.models.Status;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.md_5.bungee.api.ChatColor;

public class PlayerStatusManager {

    private FileConfiguration config;
    private final StatusManager statusManager;

    public PlayerStatusManager(FileConfiguration config, StatusManager statusManager) {
        this.config = config;
        this.statusManager = statusManager;
    }

    /**
     * Gets the player's status. If not set, returns the current default.
     */
    public String getPlayerStatusKey(Player player) {
        String key = StatusPlayerConfigFile.getConfig().getString("statuses." + player.getUniqueId());
        System.out.println(key);
        if (key == null || statusManager.getByKey(key).isEmpty()) {
            Optional<Status> defaultStatus = statusManager.getDefaultStatus();
            if (defaultStatus.isPresent() && config.getBoolean("default.use-default-on-join")) {
                key = defaultStatus.get().getKey();
            } else {
                key = null;
            }
        }
        System.out.println("getPlayerStatusKey returning: " + key);
        return key;
    }

    /**
     * Gets the player's status. If not set, returns the current default.
     */
    public Status getPlayerStatus(Player player) {
        return statusManager.getByKey(getPlayerStatusKey(player)).orElse(null);
    }

    /**
     * Saves the player's status.
     */
    public Status setPlayerStatus(Player player, String key) {
        StatusPlayerConfigFile.getConfig().set("statuses." + player.getUniqueId(), key);
        StatusPlayerConfigFile.saveConfig();
        updateDisplayName(player, key);
        return statusManager.getByKey(key).orElse(null);
    }

    /**
     * Applies the tablist format.
     */
    public void updateDisplayName(Player player, String key) {
        Optional<Status> optStatus = statusManager.getByKey(key);
        Status status;
        if (optStatus.isPresent()) {
            status = optStatus.get();
        } else {
            Bukkit.getLogger().warning(key + " is not a valid status key!");
            status = null;
        }

        if (config.getBoolean("tablist.enabled", true)) {
            String format = config.getString("tablist.format", "%status% %username%");
            format = format.replace("%status%", status != null ? status.getColoredName() : "")
                    .replace("%username%", player.getName());

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
            }

            player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', format));
        }
    }

    public boolean playerHasStatusPermission(Player player, String key) {
        String permission = "status.status." + key;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String perm = info.getPermission();

            // Explicit deny check
            if (perm.equalsIgnoreCase(permission)
                    || perm.equalsIgnoreCase("status.status.*")
                    || perm.equalsIgnoreCase("*")) {

                // If explicitly false → deny
                if (!info.getValue()) {
                    return false;
                }

                // If true → allow
                return true;
            }
        }

        // Default: allowed if not set
        return true;
    }

    /**
     * Refreshes the display names of all online players.
     */
    public void refreshAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Status status = getPlayerStatus(player);
            updateDisplayName(player, status.getKey());
        }
    }

    /**
     * Sets the config for this manager.
     */
    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

}

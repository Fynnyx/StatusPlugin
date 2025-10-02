package ch.fynnyx.statusplugin;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.fynnyx.statusplugin.commands.ReloadstatusCommand;
import ch.fynnyx.statusplugin.commands.StatusCommand;
import ch.fynnyx.statusplugin.commands.StatusTabCompletion;
import ch.fynnyx.statusplugin.commands.StatusesCommand;
import ch.fynnyx.statusplugin.listeners.Chat;
import ch.fynnyx.statusplugin.listeners.Join;
import ch.fynnyx.statusplugin.listeners.Quit;
import ch.fynnyx.statusplugin.manager.AFKManager;
import ch.fynnyx.statusplugin.manager.PlayerStatusManager;
import ch.fynnyx.statusplugin.manager.StatusManager;
import ch.fynnyx.statusplugin.placeholder.PlaceholderStatusExpansion;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;

public final class Statusplugin extends JavaPlugin {

    private FileConfiguration config;
    private StatusManager statusManager;
    private PlayerStatusManager playerStatusManager;

    @Override
    public void onEnable() {
        registerBStats(this);
        setupConfigFile();

        // Init managers
        this.statusManager = new StatusManager();
        statusManager.loadStatuses(config);

        this.playerStatusManager = new PlayerStatusManager(config, statusManager);

        new AFKManager(this, statusManager, playerStatusManager);

        registerCommands();
        registerListeners();

        registerPlaceholderAPI();

        getLogger().info(ChatColor.GOLD + "Statusplugin" + ChatColor.GREEN + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GOLD + "Statusplugin" + ChatColor.RED + " has been disabled!");
    }

    private void setupConfigFile() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.config = getConfig();
        StatusPlayerConfigFile.setup();
    }

    private void registerCommands() {
        getCommand("status").setExecutor(new StatusCommand(playerStatusManager, statusManager));
        getCommand("status").setTabCompleter(new StatusTabCompletion(statusManager));

        getCommand("statuses").setExecutor(new StatusesCommand(statusManager));

        getCommand("reloadstatus").setExecutor(new ReloadstatusCommand(this));
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new Chat(config, playerStatusManager), this);
        manager.registerEvents(new Join(config, playerStatusManager), this);
        manager.registerEvents(new Quit(config, playerStatusManager), this);
    }

    private void registerPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderStatusExpansion(this.config,this.playerStatusManager).register();
            getLogger().info(ChatColor.GOLD + "PlaceholderAPI" + ChatColor.GREEN + " has been hooked!");
        }
    }

    private void registerBStats(JavaPlugin plugin) {
        int pluginId = 15697;
        new Metrics(plugin, pluginId);
        // Metrics metrics = new Metrics(plugin, pluginId);
        // metrics.addCustomChart(new SimplePie("chart_id", () -> "My value"));
    }

    public void reloadPlugin() {
        // Reload config
        reloadConfig();
        this.config = getConfig();

        StatusPlayerConfigFile.reloadConfig();

        // Reload statuses
        statusManager.loadStatuses(config);

        // Update PlayerStatusManager with new config
        this.playerStatusManager.setConfig(config);

        // Refresh all online players
        playerStatusManager.refreshAllOnlinePlayers();

        new AFKManager(this, statusManager, playerStatusManager);

        // Re-hook PlaceholderAPI if needed
        registerPlaceholderAPI();

        getLogger().info(ChatColor.GREEN + "Statusplugin configuration reloaded!");
    }
}

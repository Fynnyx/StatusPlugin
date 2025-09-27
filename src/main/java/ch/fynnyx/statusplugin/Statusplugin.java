package ch.fynnyx.statusplugin;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ch.fynnyx.statusplugin.commands.ReloadstatusCommand;
import ch.fynnyx.statusplugin.commands.StatusCommand;
import ch.fynnyx.statusplugin.commands.StatusTabCompletion;
import ch.fynnyx.statusplugin.listeners.Chat;
import ch.fynnyx.statusplugin.listeners.Join;
import ch.fynnyx.statusplugin.listeners.Quit;
import ch.fynnyx.statusplugin.placeholder.PlaceholderStatusExpansion;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public final class Statusplugin extends JavaPlugin {

    FileConfiguration config;
    LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerBStats(this);
        setupConfigFile();
        setupLuckPerms();

        registerCommands();
        registerListeners();

        getLogger().info(ChatColor.GOLD + "Statusplugin" + ChatColor.GREEN + " has been enabled!");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderStatusExpansion(this.config).register();
            getLogger().info(ChatColor.GOLD + "PlaceholderAPI" + ChatColor.GREEN + " has been hooked!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.GOLD + "Statusplugin" + ChatColor.GREEN + " has been disabled!");
    }

    private void setupConfigFile() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.config = getConfig();
        StatusPlayerConfigFile.setup();
    }

    private void setupLuckPerms() {
        this.luckPerms = LuckPermsProvider.get();
    }

    private void registerCommands() {
        getCommand("status").setExecutor(new StatusCommand(this.config, this.luckPerms));
        getCommand("status").setTabCompleter(new StatusTabCompletion(this.config));
        getCommand("reloadstatus").setExecutor(new ReloadstatusCommand(this));
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new Chat(this.config, this.luckPerms), this);
        manager.registerEvents(new Join(this.config, this.luckPerms), this);
        manager.registerEvents(new Quit(this.config, this.luckPerms), this);
    }

    private void registerBStats(JavaPlugin plugin) {
        int pluginId = 15697;
        Metrics metrics = new Metrics(plugin, pluginId);

        metrics.addCustomChart(new SimplePie("chart_id", () -> "My value"));
    }

    public void reloadPlugin() {
        // Reload config
        reloadConfig();
        this.config = getConfig();

        registerBStats(this);
        setupLuckPerms();

        registerCommands();
        registerListeners();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderStatusExpansion(this.config).register();
            getLogger().info(ChatColor.GOLD + "PlaceholderAPI" + ChatColor.GREEN + " has been hooked!");
        }

        getLogger().info(ChatColor.GREEN + "Statusplugin configuration reloaded!");

    }
}

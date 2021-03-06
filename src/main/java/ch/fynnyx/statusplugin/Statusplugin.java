package ch.fynnyx.statusplugin;

import ch.fynnyx.statusplugin.commands.StatusCommand;
import ch.fynnyx.statusplugin.commands.StatusTabCompletion;
import ch.fynnyx.statusplugin.listeners.Chat;
import ch.fynnyx.statusplugin.listeners.Join;
import ch.fynnyx.statusplugin.listeners.Quit;
import ch.fynnyx.statusplugin.utils.StatusPlayerConfigFile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Statusplugin extends JavaPlugin {

    FileConfiguration config;
    LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerBStats(this);
        setupConfigFile();
        setupLuckPerms();
        registerListeners();

        getCommand("status").setExecutor(new StatusCommand(this.config, this.luckPerms));
        getCommand("status").setTabCompleter(new StatusTabCompletion(this.config));
        System.out.println(ChatColor.GOLD + "Statusplugin" + ChatColor.GREEN + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println(ChatColor.GOLD + "Statusplugin" + ChatColor.GREEN + " has been disabled!");
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
}

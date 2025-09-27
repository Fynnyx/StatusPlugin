package ch.fynnyx.statusplugin.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatusPlayerConfigFile {
    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Statusplugin").getDataFolder(), "playerstatuses.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        getConfig().addDefault("statuses", "");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Couldn't save config gile");
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}

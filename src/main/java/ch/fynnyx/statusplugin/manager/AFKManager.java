package ch.fynnyx.statusplugin.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AFKManager implements Listener {

    private final JavaPlugin plugin;
    private final StatusManager statusManager;
    private final PlayerStatusManager playerStatusManager;

    private final boolean enabled;
    private final long afkTimeoutMillis;
    private final String afkStatus;

    private final Map<UUID, Long> lastActive = new HashMap<>();
    private final Map<UUID, String> lastStatus = new HashMap<>();
    private final Set<UUID> afkPlayers = new HashSet<>();

    public AFKManager(JavaPlugin plugin, StatusManager statusManager, PlayerStatusManager playerStatusManager) {
        this.plugin = plugin;
        this.statusManager = statusManager;
        this.playerStatusManager = playerStatusManager;

        // read config
        int seconds = plugin.getConfig().getInt("afk.time", 5);
        this.afkTimeoutMillis = seconds * 1000L;
        String afkStatus = plugin.getConfig().getString("afk.status");
        if (afkStatus == null || statusManager.getByKey(afkStatus) == null) {
            plugin.getLogger().warning("AFK status '" + afkStatus + "' is not defined in config.yml!");
            plugin.getLogger().warning("Disabling AFK feature.");
            this.afkStatus = "AFK";
            this.enabled = false;
        } else {
            this.enabled = plugin.getConfig().getBoolean("afk.enabled", true);
            this.afkStatus = afkStatus;
        }
        if (enabled) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            startAfkChecker();
            Bukkit.getLogger().info("AFK feature enabled");
        }
    }

    /** Updates activity and resets AFK if needed */
    private void updateActivity(Player player) {
        lastActive.put(player.getUniqueId(), System.currentTimeMillis());

        if (afkPlayers.remove(player.getUniqueId())) {
            // restore last status if stored
            String oldStatus = lastStatus.remove(player.getUniqueId());
            if (oldStatus != null) {
                playerStatusManager.setPlayerStatus(player, oldStatus);
            }
        }
    }

    /** Marks the player as AFK and saves their last status */
    private void setAfk(Player player) {
        if (afkPlayers.add(player.getUniqueId())) {
            // save current status before overwriting
            String currentStatus = playerStatusManager.getPlayerStatusKey(player);
            if (currentStatus != null && !currentStatus.equalsIgnoreCase(afkStatus)) {
                lastStatus.put(player.getUniqueId(), currentStatus);
            }
            playerStatusManager.setPlayerStatus(player, afkStatus);
        }
    }

    /** Start scheduler that checks AFK every minute */
    private void startAfkChecker() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                long last = lastActive.getOrDefault(player.getUniqueId(), now);
                if (!afkPlayers.contains(player.getUniqueId()) &&
                        now - last >= afkTimeoutMillis) {
                    setAfk(player);
                }
            }
        }, 20L, 20L * 15); // run every 30 seconds
    }

    /* ---- Event Listeners ---- */

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateActivity(e.getPlayer());
        lastStatus.put(e.getPlayer().getUniqueId(), playerStatusManager.getPlayerStatusKey(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastActive.remove(e.getPlayer().getUniqueId());
        lastStatus.remove(e.getPlayer().getUniqueId());
        afkPlayers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX()
                || e.getFrom().getY() != e.getTo().getY()
                || e.getFrom().getZ() != e.getTo().getZ()) {
            updateActivity(e.getPlayer());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        updateActivity(e.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        updateActivity(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        updateActivity(e.getPlayer());
    }
}

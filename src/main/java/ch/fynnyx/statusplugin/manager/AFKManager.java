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
        if (afkStatus == null || this.statusManager.getByKey(afkStatus) == null) {
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
            System.out.println("Restoring old status: " + oldStatus);
            if (oldStatus != null) {
                playerStatusManager.setPlayerStatus(player, oldStatus);
            } else {
                playerStatusManager.setPlayerStatus(player, null);
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

    public void resetAfk(Player player) {
        if (afkPlayers.remove(player.getUniqueId())) {
            lastActive.remove(player.getUniqueId());
            // restore last status if stored
            String oldStatus = lastStatus.remove(player.getUniqueId());
            if (oldStatus != null) {
                playerStatusManager.setPlayerStatus(player, oldStatus);
            } else {
                playerStatusManager.setPlayerStatus(player, null);
            }
        }
    }

    public void resetAllAfk() {
        Integer afkCount = afkPlayers.size();
        if (afkCount == 0) {
            return;
        }
        Integer removeCount = 0;
        for (UUID uuid : new HashSet<>(afkPlayers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                resetAfk(player);
                removeCount++;
            }
        }
        Bukkit.getLogger().info("AFK status has been reset for " + removeCount + " players (of " + afkCount + " total).");
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
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        resetAfk(e.getPlayer());
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

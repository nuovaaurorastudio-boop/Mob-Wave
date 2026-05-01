package it.nuovaaurorastudio.mobwave.managers;

import it.nuovaaurorastudio.mobwave.MobWave;
import it.nuovaaurorastudio.mobwave.config.PluginConfig;
import it.nuovaaurorastudio.mobwave.spawning.MobSpawner;
import it.nuovaaurorastudio.mobwave.ui.WaveBossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.kingdoms.constants.group.Kingdom;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WaveManager implements Listener {

    private final MobWave plugin;
    private final MobSpawner spawner;
    private final Map<UUID, ActiveWave> activeWaves = new HashMap<>();
    private final NamespacedKey WAVE_MOB_KEY;

    public WaveManager(MobWave plugin) {
        this.plugin = plugin;
        this.spawner = new MobSpawner(plugin);
        this.WAVE_MOB_KEY = new NamespacedKey(plugin, "mobwave_mob");
        Bukkit.getPluginManager().registerEvents(this, plugin);
        
        // Start Global Viewers Task (Optimization: one task for all waves)
        startGlobalViewersTask();
    }

    private void startGlobalViewersTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (ActiveWave wave : activeWaves.values()) {
                    wave.updateViewers();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void checkWaves() {
        if (!plugin.isKingdomsEnabled()) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            // Placeholder logic: in production use actual Kingdom data
            UUID kingdomId = p.getUniqueId(); 
            if (activeWaves.containsKey(kingdomId)) continue;
            if (!canStartWave(kingdomId)) continue;

            if (ThreadLocalRandom.current().nextInt(100) < plugin.getPluginConfig().getSpawnChance()) {
                startWave(kingdomId, p.getLocation());
            }
        }
    }

    private boolean canStartWave(UUID kingdomId) {
        if (plugin.getDatabaseManager().getDailyCount(kingdomId) >= plugin.getPluginConfig().getWavesPerDayLimit()) return false;
        
        long lastTime = plugin.getDatabaseManager().getLastWaveTime(kingdomId);
        long cooldownMillis = plugin.getPluginConfig().getMinCooldownMinutes() * 60 * 1000L;
        return (System.currentTimeMillis() - lastTime >= cooldownMillis);
    }

    public void forceStartWave(Player p) {
        startWave(p.getUniqueId(), p.getLocation());
    }

    public void forceStartWave(String kingdomName, CommandSender sender) {
        Kingdom kingdom = Kingdom.getKingdom(kingdomName);
        if (kingdom == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getKingdomNotFound().replace("{name}", kingdomName)));
            return;
        }
        
        Location loc = null;
        if (kingdom.getHome() != null) {
             org.kingdoms.server.location.Location kLoc = kingdom.getHome();
             org.bukkit.World world = Bukkit.getWorld(kLoc.getWorld().getName());
             if (world != null) loc = new Location(world, kLoc.getX(), kLoc.getY(), kLoc.getZ());
        }

        if (loc == null) {
            sender.sendMessage(ChatColor.RED + "Kingdom location not found.");
            return;
        }

        startWave(kingdom.getId(), loc);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getWaveStarted().replace("{name}", kingdom.getName())));
    }

    private void startWave(UUID kingdomId, Location center) {
        if (activeWaves.containsKey(kingdomId)) return;

        ActiveWave wave = new ActiveWave(kingdomId, center);
        activeWaves.put(kingdomId, wave);
        plugin.getDatabaseManager().recordWave(kingdomId);
        wave.startAlert(plugin.getPluginConfig().getDelayBeforeFirstWave());
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        String kingdomIdStr = entity.getPersistentDataContainer().get(WAVE_MOB_KEY, PersistentDataType.STRING);
        if (kingdomIdStr != null) {
            try {
                UUID kingdomId = UUID.fromString(kingdomIdStr);
                ActiveWave wave = activeWaves.get(kingdomId);
                if (wave != null) wave.onMobDeath(entity);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        for (ActiveWave wave : activeWaves.values()) {
            wave.getBossBar().removePlayer(e.getPlayer());
        }
    }

    public void cleanup() {
        for (ActiveWave wave : new ArrayList<>(activeWaves.values())) {
            wave.finish(false);
        }
        activeWaves.clear();
    }

    public boolean stopWaveFor(Player p) {
        for (ActiveWave wave : new ArrayList<>(activeWaves.values())) {
            if (wave.getBossBar().hasPlayer(p)) {
                wave.finish(false);
                return true;
            }
        }
        return false;
    }

    public void stopAllWaves() {
        cleanup();
    }

    public List<String> getKingdomNames() {
        List<String> names = new ArrayList<>();
        for (Kingdom k : org.kingdoms.main.Kingdoms.get().getDataCenter().getKingdomManager().getKingdoms()) {
            names.add(k.getName());
        }
        return names;
    }

    public class ActiveWave {
        private final UUID kingdomId;
        private final Location center;
        private final WaveBossBar bossBar;
        private final List<String> waveIds;
        private final List<Entity> spawnedMobs = new ArrayList<>();
        
        private int currentWaveIndex = 0;
        private int mobsRemaining = 0;
        private int mobsAlive = 0;
        private int mobsSpawnedSoFar = 0;
        private long waveStartTime;
        private boolean isAlertPhase = false;
        
        private BukkitRunnable currentTask;

        public ActiveWave(UUID kingdomId, Location center) {
            this.kingdomId = kingdomId;
            this.center = center;
            this.bossBar = new WaveBossBar(plugin);
            this.waveIds = new ArrayList<>(plugin.getPluginConfig().getWaves().keySet());
        }

        public void updateViewers() {
            if (center.getWorld() == null) return;
            double range = plugin.getPluginConfig().getRangeFromKingdomCenter() * 1.5;
            for (Player p : center.getWorld().getPlayers()) {
                if (p.getLocation().distance(center) <= range) {
                    bossBar.addPlayer(p);
                } else {
                    bossBar.removePlayer(p);
                }
            }
        }

        public void startAlert(int seconds) {
            this.isAlertPhase = true;
            this.currentTask = new BukkitRunnable() {
                int timer = seconds;
                @Override
                public void run() {
                    if (timer <= 0) {
                        this.cancel();
                        spawnNextWave();
                        return;
                    }
                    bossBar.updateAlert(timer);
                    timer--;
                }
            };
            this.currentTask.runTaskTimer(plugin, 0L, 20L);
        }

        private void spawnNextWave() {
            if (currentWaveIndex >= waveIds.size()) {
                finish(true);
                return;
            }

            isAlertPhase = false;
            String waveId = waveIds.get(currentWaveIndex);
            PluginConfig.WaveData data = plugin.getPluginConfig().getWaves().get(waveId);
            
            this.mobsRemaining = data.getTotalMobs();
            this.mobsSpawnedSoFar = 0;
            this.mobsAlive = 0;
            this.waveStartTime = System.currentTimeMillis();
            
            bossBar.updateProgress(currentWaveIndex + 1, waveIds.size(), mobsRemaining, 1.0f);
            startSpawningTask(data);
        }

        private void startSpawningTask(PluginConfig.WaveData data) {
            this.currentTask = new BukkitRunnable() {
                @Override
                public void run() {
                    // Timeout Check
                    if (System.currentTimeMillis() - waveStartTime > plugin.getPluginConfig().getWaveTimeoutSeconds() * 1000L) {
                        finish(false);
                        return;
                    }

                    if (mobsRemaining <= 0) {
                        this.cancel();
                        return;
                    }

                    int maxConcurrent = plugin.getPluginConfig().getMaxConcurrentMobs();
                    int range = plugin.getPluginConfig().getRangeFromKingdomCenter();

                    while (mobsAlive < maxConcurrent && mobsSpawnedSoFar < data.getTotalMobs()) {
                        String mobType = pickMobType(data.getMobs());
                        Entity entity = spawner.spawnMob(center, mobType, range);
                        if (entity != null) {
                            entity.getPersistentDataContainer().set(WAVE_MOB_KEY, PersistentDataType.STRING, kingdomId.toString());
                            spawnedMobs.add(entity);
                            mobsAlive++;
                            mobsSpawnedSoFar++;
                        } else break;
                    }
                }
            };
            this.currentTask.runTaskTimer(plugin, 0L, plugin.getPluginConfig().getSpawnIntervalTicks());
        }

        private String pickMobType(Map<String, Integer> chances) {
            int roll = ThreadLocalRandom.current().nextInt(100) + 1;
            int current = 0;
            for (Map.Entry<String, Integer> entry : chances.entrySet()) {
                current += entry.getValue();
                if (roll <= current) return entry.getKey();
            }
            return chances.keySet().iterator().next();
        }

        public void onMobDeath(Entity entity) {
            if (isAlertPhase) return;
            spawnedMobs.remove(entity);
            mobsRemaining--;
            mobsAlive--;
            
            String waveId = waveIds.get(currentWaveIndex);
            PluginConfig.WaveData data = plugin.getPluginConfig().getWaves().get(waveId);
            bossBar.updateProgress(currentWaveIndex + 1, waveIds.size(), mobsRemaining, (float) mobsRemaining / data.getTotalMobs());

            if (mobsRemaining <= 0) {
                if (currentTask != null) currentTask.cancel();
                currentWaveIndex++;
                if (currentWaveIndex >= waveIds.size()) {
                    finish(true);
                } else {
                    startAlert(data.getDelayToNextWave());
                }
            }
        }

        public void finish(boolean success) {
            activeWaves.remove(kingdomId);
            bossBar.cleanup();
            if (currentTask != null) currentTask.cancel();
            
            // Cleanup mobs if not success
            if (!success) {
                for (Entity entity : spawnedMobs) {
                    if (entity.isValid()) entity.remove();
                }
            }
            spawnedMobs.clear();
        }

        public WaveBossBar getBossBar() { return bossBar; }
    }
}

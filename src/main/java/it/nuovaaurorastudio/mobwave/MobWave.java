package it.nuovaaurorastudio.mobwave;

import co.aikar.commands.PaperCommandManager;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import it.nuovaaurorastudio.mobwave.commands.MobWaveCommand;
import it.nuovaaurorastudio.mobwave.config.MessagesConfig;
import it.nuovaaurorastudio.mobwave.config.PluginConfig;
import it.nuovaaurorastudio.mobwave.database.DatabaseManager;
import it.nuovaaurorastudio.mobwave.managers.WaveManager;
import it.nuovaaurorastudio.mobwave.scheduler.MobWaveScheduler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

@Getter
public class MobWave extends JavaPlugin {

    private static MobWave instance;
    
    private PluginConfig pluginConfig;
    private MessagesConfig messagesConfig;
    private DatabaseManager databaseManager;
    private WaveManager waveManager;
    private PaperCommandManager commandManager;

    private boolean mythicMobsEnabled = false;
    private boolean kingdomsEnabled = false;

    @Override
    public void onEnable() {
        instance = this;

        // Load Configs
        try {
            this.pluginConfig = ConfigManager.create(PluginConfig.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(new File(getDataFolder(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
            this.messagesConfig = ConfigManager.create(MessagesConfig.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(new File(getDataFolder(), "messages.yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load configurations!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Database
        this.databaseManager = new DatabaseManager(this);

        // Hook Dependencies
        hookMythicMobs();
        hookKingdoms();

        if (!kingdomsEnabled) {
            getLogger().severe("KingdomsX not found! This plugin requires KingdomsX to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize Managers
        this.waveManager = new WaveManager(this);

        // Start Scheduler
        long interval = pluginConfig.getWaveScheduleSeconds() * 20L;
        new MobWaveScheduler(this, waveManager).runTaskTimer(this, interval, interval);

        // Register Commands with ACF
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new MobWaveCommand(this));
        
        // Completions
        this.commandManager.getCommandCompletions().registerAsyncCompletion("kingdoms", c -> waveManager.getKingdomNames());

        getLogger().info("MobWave has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (waveManager != null) {
            waveManager.cleanup();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("MobWave has been disabled.");
    }

    private void hookMythicMobs() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            this.mythicMobsEnabled = true;
            getLogger().info("Hooked into MythicMobs!");
        }
    }

    private void hookKingdoms() {
        if (Bukkit.getPluginManager().isPluginEnabled("Kingdoms")) {
            this.kingdomsEnabled = true;
            getLogger().info("Hooked into KingdomsX!");
        }
    }

    public void reloadPlugin() {
        pluginConfig.load();
        messagesConfig.load();
        
        // Restart Scheduler
        Bukkit.getScheduler().cancelTasks(this);
        long interval = pluginConfig.getWaveScheduleSeconds() * 20L;
        new MobWaveScheduler(this, waveManager).runTaskTimer(this, interval, interval);
        
        getLogger().info("MobWave reloaded successfully.");
    }

    public static MobWave getInstance() {
        return instance;
    }
}

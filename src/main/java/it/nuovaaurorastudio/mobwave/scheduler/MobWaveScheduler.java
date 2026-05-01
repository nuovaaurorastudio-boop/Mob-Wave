package it.nuovaaurorastudio.mobwave.scheduler;

import it.nuovaaurorastudio.mobwave.MobWave;
import it.nuovaaurorastudio.mobwave.managers.WaveManager;
import org.bukkit.scheduler.BukkitRunnable;

public class MobWaveScheduler extends BukkitRunnable {

    private final MobWave plugin;
    private final WaveManager waveManager;

    public MobWaveScheduler(MobWave plugin, WaveManager waveManager) {
        this.plugin = plugin;
        this.waveManager = waveManager;
    }

    @Override
    public void run() {
        if (!plugin.isEnabled()) {
            this.cancel();
            return;
        }
        
        // Logic already runs on main thread, no need to schedule another task
        waveManager.checkWaves();
    }
}

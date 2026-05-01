package it.nuovaaurorastudio.mobwave.ui;

import it.nuovaaurorastudio.mobwave.MobWave;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class WaveBossBar {

    private final MobWave plugin;
    private final BossBar bossBar;
    private final Set<Player> viewers = new HashSet<>();

    public WaveBossBar(MobWave plugin) {
        this.plugin = plugin;
        this.bossBar = BossBar.bossBar(
                Component.empty(),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
    }

    public void updateAlert(int timeRemaining) {
        bossBar.color(BossBar.Color.YELLOW);
        String title = plugin.getMessagesConfig().getWaveAlertTitle();
        String subtitle = plugin.getMessagesConfig().getWaveAlertSubtitle().replace("{time}", String.valueOf(timeRemaining));
        
        bossBar.name(deserialize(title + " " + subtitle));
        bossBar.progress(1.0f);
    }

    public void updateProgress(int currentWave, int totalWaves, int mobsRemaining, float progress) {
        bossBar.color(BossBar.Color.RED);
        String title = plugin.getMessagesConfig().getWaveActiveTitle()
                .replace("{current}", String.valueOf(currentWave))
                .replace("{total}", String.valueOf(totalWaves));
        String subtitle = plugin.getMessagesConfig().getWaveActiveSubtitle()
                .replace("{count}", String.valueOf(mobsRemaining));

        bossBar.name(deserialize(title + " " + subtitle));
        bossBar.progress(Math.max(0.0f, Math.min(1.0f, progress)));
    }

    public void addPlayer(Player p) {
        if (viewers.add(p)) {
            p.showBossBar(bossBar);
        }
    }

    public void removePlayer(Player p) {
        if (viewers.remove(p)) {
            p.hideBossBar(bossBar);
        }
    }
    
    public void cleanup() {
        for (Player p : viewers) {
            p.hideBossBar(bossBar);
        }
        viewers.clear();
    }
    
    public boolean hasPlayer(Player p) {
        return viewers.contains(p);
    }

    private Component deserialize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}

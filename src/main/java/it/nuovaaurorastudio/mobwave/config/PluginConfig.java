package it.nuovaaurorastudio.mobwave.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.NONE)
public class PluginConfig extends OkaeriConfig {

    @Comment("Check interval for potential waves (in seconds)")
    private long waveScheduleSeconds = 600;

    @Comment("Range from kingdom center for mob spawning")
    private int rangeFromKingdomCenter = 50;

    @Comment("Delay before the first wave starts (in seconds)")
    private int delayBeforeFirstWave = 30;

    @Comment("Maximum number of concurrent mobs active per wave")
    private int maxConcurrentMobs = 20;

    @Comment("Maximum limit of waves per kingdom per day")
    private int wavesPerDayLimit = 3;

    @Comment("Minimum cooldown between waves (in minutes)")
    private int minCooldownMinutes = 120;

    @Comment("Spawn chance percentage for a wave (0-100)")
    private int spawnChance = 50;

    @Comment("Spawn interval between mobs (in ticks)")
    private int spawnIntervalTicks = 40;

    @Comment("Maximum timeout to complete a wave (in seconds)")
    private int waveTimeoutSeconds = 600;

    @Comment("Message language")
    private String language = "en";

    @Comment("Wave definitions")
    private Map<String, WaveData> waves = new HashMap<>() {{
        put("wave1", new WaveData(10, 30, new HashMap<>() {{
            put("ZOMBIE", 100);
        }}));
        put("wave2", new WaveData(15, 0, new HashMap<>() {{
            put("ZOMBIE", 50);
            put("SKELETON", 50);
        }}));
    }};

    @Getter
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.NONE)
    public static class WaveData extends OkaeriConfig {
        private int totalMobs;
        private int delayToNextWave;
        private Map<String, Integer> mobs;

        public WaveData() {}

        public WaveData(int totalMobs, int delayToNextWave, Map<String, Integer> mobs) {
            this.totalMobs = totalMobs;
            this.delayToNextWave = delayToNextWave;
            this.mobs = mobs;
        }
    }
}

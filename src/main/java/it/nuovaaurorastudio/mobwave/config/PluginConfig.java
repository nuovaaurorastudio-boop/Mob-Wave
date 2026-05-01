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

    @Comment("Intervallo di controllo per le potenziali ondate (in secondi)")
    private long waveScheduleSeconds = 600;

    @Comment("Raggio dal centro del regno per lo spawn dei mob")
    private int rangeFromKingdomCenter = 50;

    @Comment("Ritardo prima dell'inizio della prima ondata (in secondi)")
    private int delayBeforeFirstWave = 30;

    @Comment("Numero massimo di mob contemporaneamente attivi per ondata")
    private int maxConcurrentMobs = 20;

    @Comment("Limite massimo di ondate per regno al giorno")
    private int wavesPerDayLimit = 3;

    @Comment("Cooldown minimo tra le ondate (in minuti)")
    private int minCooldownMinutes = 120;

    @Comment("Percentuale di probabilità di spawn di un'ondata (0-100)")
    private int spawnChance = 50;

    @Comment("Intervallo di spawn tra i mob (in ticks)")
    private int spawnIntervalTicks = 40;

    @Comment("Timeout massimo per completare un'ondata (in secondi)")
    private int waveTimeoutSeconds = 600;

    @Comment("Lingua dei messaggi")
    private String language = "it";

    @Comment("Definizione delle ondate")
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

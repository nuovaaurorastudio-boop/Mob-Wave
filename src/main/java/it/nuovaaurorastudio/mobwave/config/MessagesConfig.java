package it.nuovaaurorastudio.mobwave.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;

@Getter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.NONE)
public class MessagesConfig extends OkaeriConfig {

    private String prefix = "&8[&6MobWave&8] &r";
    
    private String waveAlertTitle = "&eINCOMING WAVE!";
    private String waveAlertSubtitle = "&fStarts in &6{time} &fseconds.";
    
    private String waveActiveTitle = "&cWave {current}/{total}";
    private String waveActiveSubtitle = "&7Mobs Remaining: &c{count}";
    
    private String waveComplete = "&aWave Completed!";
    private String eventComplete = "&aThe Kingdom has survived the assault!";
    
    private String noPermission = "&cYou do not have permission to execute this command.";
    private String commandUsage = "&cUsage: {usage}";
    private String configReloaded = "&aConfiguration reloaded successfully!";
    private String kingdomNotFound = "&cKingdom not found: {name}";
    private String waveStarted = "&aWave started for kingdom: {name}";
    private String waveStartedLocation = "&aWave started at your location!";
    private String noWaveFound = "&cNo active wave found nearby.";
    private String waveStopped = "&aWave stopped!";
}

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
    
    private String waveAlertTitle = "&eONDA IMMINENTE!";
    private String waveAlertSubtitle = "&fInizio tra &6{time} &fsecondi.";
    
    private String waveActiveTitle = "&cOnda {current}/{total}";
    private String waveActiveSubtitle = "&7Mob Rimanenti: &c{count}";
    
    private String waveComplete = "&aOnda Completata!";
    private String eventComplete = "&aIl Regno ha sopravvissuto all'assalto!";
    
    private String noPermission = "&cNon hai il permesso per eseguire questo comando.";
    private String commandUsage = "&cUtilizzo: {usage}";
    private String configReloaded = "&aConfigurazione ricaricata con successo!";
    private String kingdomNotFound = "&cRegno non trovato: {name}";
    private String waveStarted = "&aOnda avviata per il regno: {name}";
    private String waveStartedLocation = "&aOnda avviata nella tua posizione!";
    private String noWaveFound = "&cNessuna ondata attiva trovata nelle vicinanze.";
    private String waveStopped = "&aOnda interrotta!";
}

package it.nuovaaurorastudio.mobwave.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import it.nuovaaurorastudio.mobwave.MobWave;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mobwave|mw")
public class MobWaveCommand extends BaseCommand {

    private final MobWave plugin;

    public MobWaveCommand(MobWave plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @Subcommand("help")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== MobWave Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/mw start " + ChatColor.WHITE + "- Start a wave at your location");
        sender.sendMessage(ChatColor.YELLOW + "/mw stop " + ChatColor.WHITE + "- Stop the wave you are currently in");
        sender.sendMessage(ChatColor.YELLOW + "/mw reload " + ChatColor.WHITE + "- Reload the plugin configuration");
        sender.sendMessage(ChatColor.YELLOW + "/mw spawn <kingdom> " + ChatColor.WHITE + "- Spawn a wave for a specific kingdom");
    }

    @Subcommand("start")
    @CommandPermission("mobwave.admin")
    public void onStart(Player player) {
        plugin.getWaveManager().forceStartWave(player);
        player.sendMessage(color(plugin.getMessagesConfig().getWaveStartedLocation()));
    }

    @Subcommand("stop")
    @CommandPermission("mobwave.admin")
    public void onStop(CommandSender sender) {
        if (sender instanceof Player player) {
            if (plugin.getWaveManager().stopWaveFor(player)) {
                sender.sendMessage(color(plugin.getMessagesConfig().getWaveStopped()));
            } else {
                sender.sendMessage(color(plugin.getMessagesConfig().getNoWaveFound()));
            }
        } else {
            plugin.getWaveManager().stopAllWaves();
            sender.sendMessage(color(plugin.getMessagesConfig().getWaveStopped()));
        }
    }

    @Subcommand("spawn")
    @CommandPermission("mobwave.admin")
    @CommandCompletion("@kingdoms")
    public void onSpawn(CommandSender sender, String kingdomName) {
        plugin.getWaveManager().forceStartWave(kingdomName, sender);
    }

    @Subcommand("reload")
    @CommandPermission("mobwave.admin")
    public void onReload(CommandSender sender) {
        plugin.reloadPlugin();
        sender.sendMessage(color(plugin.getMessagesConfig().getConfigReloaded()));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}

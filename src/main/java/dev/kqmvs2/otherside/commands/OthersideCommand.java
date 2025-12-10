package dev.kqmvs2.otherside.commands;

import dev.kqmvs2.otherside.Otherside;
import dev.kqmvs2.otherside.OthersideLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;

public class OthersideCommand implements TabExecutor {
    Otherside plugin;

    public OthersideCommand(Otherside plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        final String USAGE_MSG = ChatColor.YELLOW + "Usage: /otherside <reload|verbose>";

        if (args.length == 1) {
            String subcommandStr = args[0];
            switch (subcommandStr) {
                case "reload":
                    doReloadSubcommand(sender);
                    break;
                case "verbose":
                    doVerboseSubcommand(sender);
                    break;
                default:
                    sender.sendMessage(USAGE_MSG + ". Unknown subcommand.");
            }
        } else {
            sender.sendMessage(USAGE_MSG + ". Must have exact one argument.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            completions.add("verbose");
            return completions;
        }
        return null;
    }

    private void doReloadSubcommand(CommandSender sender) {
        final String RELOADED_MSG = ChatColor.GREEN + "Configuration reloaded";
        final String RELOAD_ERROR_MSG = ChatColor.RED + "Error reloading config: %s. Some changes may not have been applied, " +
                "check the console for more details";
        try {
            plugin.getOthersideConfig().loadAndParseConfig();
            sender.sendMessage(RELOADED_MSG);
        } catch (InvalidConfigurationException ex) {
            sender.sendMessage(String.format(RELOAD_ERROR_MSG, ex.getMessage()));
            this.plugin.getOthersideLogger().getLogger().log(Level.SEVERE, RELOAD_ERROR_MSG, ex);
        }
    }

    private void doVerboseSubcommand(CommandSender sender) {
        final String VERBOSE_MODE_OFF = "[OTHERSIDE] Verbose mode off. To enable, run /otherside verbose";
        final String VERBOSE_MODE_ON = "[OTHERSIDE] Verbose mode on. To disable, run /otherside verbose";
        OthersideLogger othersideLogger = plugin.getOthersideLogger();

        if (sender instanceof org.bukkit.entity.Player player) {
            if (othersideLogger.isPlayerReceivingVerboseLogs(player)) {
                player.sendMessage(VERBOSE_MODE_OFF);
                othersideLogger.removePlayerDestination(player);
                othersideLogger.removeConsoleDestination();
            } else {
                player.sendMessage(VERBOSE_MODE_ON);
                othersideLogger.addDestination(player);
                othersideLogger.addDestination(Bukkit.getConsoleSender());
            }
        } else if (sender instanceof ConsoleCommandSender console) {
            if (othersideLogger.isConsoleReceivingVerboseLogs()) {
                othersideLogger.getLogger().info(VERBOSE_MODE_OFF);
                othersideLogger.removeConsoleDestination();
            } else {
                othersideLogger.getLogger().info(VERBOSE_MODE_ON);
                othersideLogger.addDestination(console);
            }
        }
    }
}
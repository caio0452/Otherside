package dev.kqmvs2.otherside;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;

public class OthersideCommand implements TabExecutor {
    Otherside plugin;

    public OthersideCommand(Otherside plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String USAGE_MSG = ChatColor.YELLOW + "Usage: /otherside reload";

        switch (args.length) {
            case 1:
                if (args[0].equals("reload")) {
                   doReloadSubcommand(sender);
                }
                break;
            default:
                sender.sendMessage(USAGE_MSG);
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
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
            ex.printStackTrace();
        }
    }
}
package dev.kqmvs2.otherside;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class OthersideLogger {
    private final List<Player> playerDestinations;
    private final List<org.bukkit.command.ConsoleCommandSender> consoleDestinations;
    private final Logger logger;

    public OthersideLogger(Plugin plugin) {
        this.playerDestinations = new ArrayList<>();
        this.consoleDestinations = new ArrayList<>();
        this.logger = plugin.getLogger();
    }

    public Logger getLogger() {
        return logger;
    }

    public void addDestination(org.bukkit.command.ConsoleCommandSender console) {
        if (!consoleDestinations.contains(console)) {
            consoleDestinations.add(console);
        }
    }

    public void addDestination(Player player) {
        if (!playerDestinations.contains(player)) {
            playerDestinations.add(player);
        }
    }

    public void removeConsoleDestination() {
        consoleDestinations.clear();
    }

    public void removePlayerDestination(Player player) {
        playerDestinations.removeIf(
                playerReceivingLogs -> playerReceivingLogs.getUniqueId().equals(player.getUniqueId())
        );
    }

    public boolean isPlayerReceivingVerboseLogs(Player player) {
        for (Player p : playerDestinations) {
            if (p.getUniqueId() == player.getUniqueId())   {
                return true;
            }
        }
        return false;
    }

    public boolean isConsoleReceivingVerboseLogs() {
        return !consoleDestinations.isEmpty();
    }

    public void logBecamePersistent(Entity entity) {
        sendMessage(formatEntityInfo(entity) + " became persistent");
    }

    public void logStoppedBeingPersistent(Entity entity) {
        sendMessage(formatEntityInfo(entity) + " is no longer persistent");
    }

    private String formatEntityInfo(Entity entity) {
        String nameStr;
        if (entity.getName().isEmpty()) {
            nameStr = "";
        } else {
            nameStr = " (" + entity.getName() + ")";
        }
        return String.format("%s%s @ %s(%.1f, %.1f, %.1f)",
                entity.getType(),
                nameStr,
                entity.getWorld().getName(),
                entity.getLocation().getX(),
                entity.getLocation().getY(),
                entity.getLocation().getZ()
        );
    }

    private void sendMessage(String message) {
        for (Player player : playerDestinations) {
            player.sendMessage(message);
        }
        for (ConsoleCommandSender console : consoleDestinations) {
            console.sendMessage(message);
        }
    }
}

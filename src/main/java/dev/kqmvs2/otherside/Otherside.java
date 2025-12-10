package dev.kqmvs2.otherside;

import org.bukkit.plugin.java.JavaPlugin;
import dev.kqmvs2.otherside.commands.OthersideCommand;
import dev.kqmvs2.otherside.listeners.EntityListeners;
import dev.kqmvs2.otherside.despawning.DespawnImmunityExpirer;
import dev.kqmvs2.otherside.despawning.DespawnImmunityManager;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.Objects;
import java.util.logging.Level;

public final class Otherside extends JavaPlugin {

    private OthersideConfig config;
    private OthersideLogger othersideLogger;

    @Override
    public void onEnable() {
        config = new OthersideConfig(this);
        othersideLogger = new OthersideLogger(this);

        DespawnImmunityManager despawnImmunityManager = new DespawnImmunityManager(this);
        EntityListeners entityListeners = new EntityListeners(despawnImmunityManager, othersideLogger);

        try {
            config.loadAndParseConfig();
        } catch (InvalidConfigurationException ex) {
            getOthersideLogger().getLogger().log(Level.SEVERE, "Invalid config.yml. The plugin will shut down.", ex);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(entityListeners, this);
        new DespawnImmunityExpirer(despawnImmunityManager).runTaskTimer(this, 0L, 20L);
        Objects.requireNonNull(this.getCommand("otherside")).setExecutor(new OthersideCommand(this));
    }

    public OthersideConfig getOthersideConfig() {
        return config;
    }

    public OthersideLogger getOthersideLogger() {
        return othersideLogger;
    }
}

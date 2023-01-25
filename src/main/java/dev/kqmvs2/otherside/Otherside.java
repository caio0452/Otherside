package dev.kqmvs2.otherside;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public final class Otherside extends JavaPlugin {

    private OthersideConfig config;

    @Override
    public void onEnable() {
        config = new OthersideConfig(this);

        DespawnImmunityManager despawnImmunityManager = new DespawnImmunityManager(this);
        EntityListeners entityListeners = new EntityListeners(despawnImmunityManager);

        try {
            config.loadAndParseConfig();
        } catch (InvalidConfigurationException ex) {
            this.getLogger().severe("Invalid config.yml. The plugin will shut down.");
            ex.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(entityListeners, this);
        new DespawnImmunityExpirer(despawnImmunityManager).runTaskTimer(this, 0L, 1 * 20L);

        this.getCommand("otherside").setExecutor(new OthersideCommand(this));
    }

    public OthersideConfig getOthersideConfig() {
        return config;
    }
}

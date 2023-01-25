package dev.kqmvs2.otherside;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Locale;

public class OthersideConfig {
    private final Otherside plugin;
    private int defaultDespawnTime;
    private final HashMap<EntityType, Integer> mobTypeDespawnTimeMap = new HashMap<>();

    public OthersideConfig(Otherside plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    // TODO: better error and config handling
    public void loadAndParseConfig() throws InvalidConfigurationException {
        final String DEFAULT_DESPAWN_IMMUNITY_TIME_KEY = "default-despawn-immunity-time-seconds";
        final String IMMUNITY_TIME_OVERRIDES_KEY = "despawn-immunity-time-overrides";

        plugin.reloadConfig();

        defaultDespawnTime = plugin.getConfig().getInt(DEFAULT_DESPAWN_IMMUNITY_TIME_KEY);

        ConfigurationSection despawnSection = plugin.getConfig().getConfigurationSection(IMMUNITY_TIME_OVERRIDES_KEY);
        boolean hasInvalidMobTypes = false;
        for (String entityTypeKey : despawnSection.getKeys(false)) {
            try {
                EntityType entityType = EntityType.valueOf(entityTypeKey.toUpperCase(Locale.ROOT).replace(" ", "_"));
                int despawnTimeSec = plugin.getConfig().getInt(IMMUNITY_TIME_OVERRIDES_KEY + "." + entityTypeKey);
                mobTypeDespawnTimeMap.put(entityType, despawnTimeSec);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().severe("Invalid mob type '" + entityTypeKey + "'");
                hasInvalidMobTypes = true;
            }
        }

        if (hasInvalidMobTypes) {
            throw new InvalidConfigurationException("Found invalid mob type");
        }
    }

    public int getDespawnSeconds(EntityType entityType) {
        return mobTypeDespawnTimeMap.getOrDefault(entityType, defaultDespawnTime);
    }
}

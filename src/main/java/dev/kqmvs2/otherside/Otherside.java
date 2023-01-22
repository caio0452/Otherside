package dev.kqmvs2.otherside;

import org.bukkit.plugin.java.JavaPlugin;

public final class Otherside extends JavaPlugin {
    @Override
    public void onEnable() {
        EntityListeners entityListeners = new EntityListeners(new HardDespawnManager(this));
        getServer().getPluginManager().registerEvents(entityListeners, this);
    }
}

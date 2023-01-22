package dev.kqmvs2.otherside;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class HardDespawnManager {
    private final Otherside plugin;
    private final NamespacedKey PERSISTENT_KEY;

    // TODO: make configurable
    private static final int EXEMPTION_TIME_SECONDS = 20;

    public HardDespawnManager(Otherside plugin) {
        this.plugin = plugin;
        this.PERSISTENT_KEY = new NamespacedKey(plugin, "hard-despawn-exempt-since");
    }

    public void setExemptFromHardDespawn(LivingEntity entity, boolean exempt) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (exempt) {
            long unixEpoch = System.currentTimeMillis() / 1000L;
            pdc.set(PERSISTENT_KEY, PersistentDataType.LONG, unixEpoch);
            entity.setRemoveWhenFarAway(false);
        } else {
            // TODO: should I apply manual hard despawn logic in here?
            if (pdc.has(PERSISTENT_KEY, PersistentDataType.LONG)) {
                pdc.remove(PERSISTENT_KEY);
                entity.setRemoveWhenFarAway(true);
            }
        }
    }

    public Optional<Instant> getHardDespawnExemptionTimestamp(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(PERSISTENT_KEY, PersistentDataType.LONG)) {
            return Optional.empty();
        }
        long unixEpoch = pdc.get(PERSISTENT_KEY, PersistentDataType.LONG);
        return Optional.of(Instant.ofEpochSecond(unixEpoch));
    }

    public void removeHardDespawnExemptionIfTimesUp(LivingEntity entity) {
        getHardDespawnExemptionTimestamp(entity).ifPresent(timestamp -> {
            long secondsElapsed = Duration.between(timestamp, Instant.now()).getSeconds();
            if (secondsElapsed >= EXEMPTION_TIME_SECONDS) {
                setExemptFromHardDespawn(entity, false);
            }
        });
    }

    public void scheduleExemptionRemovalWhenTimesUp(LivingEntity entity) {
        // TODO: not consistent with real time, but not a big deal
        new BukkitRunnable() {
            @Override
            public void run() {
                setExemptFromHardDespawn(entity, false);
            }
        }.runTaskLater(plugin,EXEMPTION_TIME_SECONDS  * 20L);

    }
}
package dev.kqmvs2.otherside;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class DespawnImmunityManager {
    private final NamespacedKey HARD_DESPAWN_EXEMPT_SINCE_KEY;
    private final Queue<DespawnImmunityRemovalTask> exemptionRemovalTasks = new LinkedList<>();
    private final Otherside plugin;

    public DespawnImmunityManager(Otherside plugin) {
        this.HARD_DESPAWN_EXEMPT_SINCE_KEY = new NamespacedKey(plugin, "hard-despawn-exempt-since");
        this.plugin = plugin;
    }

    public void setImmuneToHardDespawn(LivingEntity entity, boolean immune) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (immune) {
            if (!entity.getRemoveWhenFarAway()) return;
            long unixEpoch = System.currentTimeMillis() / 1000L;
            pdc.set(HARD_DESPAWN_EXEMPT_SINCE_KEY, PersistentDataType.LONG, unixEpoch);
            entity.setRemoveWhenFarAway(false);
        } else {
            if (pdc.has(HARD_DESPAWN_EXEMPT_SINCE_KEY, PersistentDataType.LONG)) {
                pdc.remove(HARD_DESPAWN_EXEMPT_SINCE_KEY);
                entity.setRemoveWhenFarAway(true);
            }
        }
    }

    public Optional<Instant> getHardDespawnImmuneSinceTimestamp(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(HARD_DESPAWN_EXEMPT_SINCE_KEY, PersistentDataType.LONG)) {
            return Optional.empty();
        }
        long unixEpoch = pdc.get(HARD_DESPAWN_EXEMPT_SINCE_KEY, PersistentDataType.LONG);
        return Optional.of(Instant.ofEpochSecond(unixEpoch));
    }

    public void processPendingImmunityRemovalTasks() {
        DespawnImmunityRemovalTask task;
        while ((task = exemptionRemovalTasks.peek()) != null && task.isDue()) {
            setImmuneToHardDespawn(task.getEntity(), false);
            exemptionRemovalTasks.poll();
        }
    }

    public void removeDespawnImmunityIfExpired(LivingEntity entity) {
        getHardDespawnImmuneSinceTimestamp(entity).ifPresent(timestamp -> {
            long secondsElapsed = Duration.between(timestamp, Instant.now()).getSeconds();
            if (secondsElapsed >= plugin.getOthersideConfig().getDespawnSeconds(entity.getType())) {
                setImmuneToHardDespawn(entity, false);
            }
        });
    }

    public void enqueueImmunityRemoval(LivingEntity entity) {
        Instant removalTimestamp = Instant.now().plusSeconds(plugin.getOthersideConfig().getDespawnSeconds(entity.getType()));
        exemptionRemovalTasks.add(new DespawnImmunityRemovalTask(entity, removalTimestamp));
    }
}
package dev.kqmvs2.otherside;

import org.bukkit.entity.LivingEntity;

import java.time.Instant;

public class DespawnImmunityRemovalTask {
    private final LivingEntity entity;
    private final Instant dueTimestamp;

    public DespawnImmunityRemovalTask(LivingEntity entity, Instant dueTimestamp) {
        this.entity = entity;
        this.dueTimestamp = dueTimestamp;
    }

    public boolean isDue() {
        return Instant.now().isAfter(dueTimestamp);
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
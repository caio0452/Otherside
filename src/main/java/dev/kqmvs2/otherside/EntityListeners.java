package dev.kqmvs2.otherside;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

public class EntityListeners implements Listener {
    HardDespawnManager despawnManager;

    public EntityListeners(HardDespawnManager despawnManager) {
        this.despawnManager = despawnManager;
    }

    @EventHandler
    public void on(EntityPortalEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            despawnManager.setExemptFromHardDespawn(livingEntity, true);
            despawnManager.scheduleExemptionRemovalWhenTimesUp(livingEntity);
        }
    }

    @EventHandler
    public void on(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.getRemoveWhenFarAway()) {
                despawnManager.removeHardDespawnExemptionIfTimesUp((LivingEntity) entity);
            }
        }
    }
}
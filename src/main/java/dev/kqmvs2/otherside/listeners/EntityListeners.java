package dev.kqmvs2.otherside.listeners;

import dev.kqmvs2.otherside.despawning.DespawnImmunityManager;
import dev.kqmvs2.otherside.OthersideLogger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityListeners implements Listener {
    private final DespawnImmunityManager despawnManager;
    private final OthersideLogger logger;

    public EntityListeners(DespawnImmunityManager despawnManager, OthersideLogger logger) {
        this.despawnManager = despawnManager;
        this.logger = logger;
    }

    @EventHandler
    public void on(EntityPortalEvent event) {
        List<Entity> allPortalingEntities = new ArrayList<>(event.getEntity().getPassengers());
        allPortalingEntities.add(event.getEntity());
        for (Entity entity : allPortalingEntities) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.getRemoveWhenFarAway()) {
                despawnManager.setImmuneToHardDespawn(livingEntity, true);
                despawnManager.enqueueImmunityRemoval(livingEntity);
            }
        }
    }

    @EventHandler
    public void on(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof LivingEntity livingEntity && !livingEntity.getRemoveWhenFarAway()) {
                despawnManager.removeDespawnImmunityIfExpired(livingEntity);
            }
        }
    }
}

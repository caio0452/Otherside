package dev.kqmvs2.otherside;

import org.bukkit.scheduler.BukkitRunnable;

public class DespawnImmunityExpirer extends BukkitRunnable {
    private final DespawnImmunityManager hardDespawnManager;

    public DespawnImmunityExpirer(DespawnImmunityManager hardDespawnManager) {
        this.hardDespawnManager = hardDespawnManager;
    }

    public void run() {
        hardDespawnManager.processPendingImmunityRemovalTasks();
    }
}

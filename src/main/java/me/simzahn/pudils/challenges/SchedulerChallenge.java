package me.simzahn.pudils.challenges;

import org.bukkit.scheduler.BukkitRunnable;

public interface SchedulerChallenge extends Challenge{

    //how often !IN SECONDS! the task should be repeated
    public float getPeriod();
    public BukkitRunnable getRunnable();

}

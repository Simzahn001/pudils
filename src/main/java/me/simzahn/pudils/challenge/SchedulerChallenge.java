package me.simzahn.pudils.challenge;

import org.bukkit.scheduler.BukkitRunnable;

public interface SchedulerChallenge extends Challenge{

    //how often !IN SECONDS! the task should be repeated
    public float getPeriod();

    //the task that should be executed
    //it's automatically stopped when the timer is not active anymore
    public BukkitRunnable getRunnable();

}

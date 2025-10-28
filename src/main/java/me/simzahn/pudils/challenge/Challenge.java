package me.simzahn.pudils.challenge;

import org.bukkit.inventory.ItemStack;

public interface Challenge {

    /*
     * You can split up the Challenges into two types:
     *  - Listener-triggered challenges
     *  - Scheduler-triggered challenges
     *
     * Implement one of these three to create a new Challenge.
     * Do not forget to register your challenge!
     *
     * To get all Players, which are playing, use Timer#getPlayers();
     * The list is reloaded every time the timer is started.
     */

    //this is the name for the database
    //if the name is changed, a new record will be added to the database with the "new" challenge
    public String getName();

    //this is the item used for displaying in Minecraft (with color-codes)
    public ItemStack getItem();

    //this is a method, which is called when the timer ist started
    default public void onStart() { }

    //this is a method, which is called when the timer ist stopped
    default public void onStop() { }





}

package me.simzahn.pudils.challenges;

import org.bukkit.inventory.ItemStack;

public interface Challenge {

    /*
     * You can split up the Challenges into two types:
     *  - Listener-triggered challenges
     *  - Scheduler-triggered challenges
     *
     * Implement one of these to create a new Challenge.
     * Do not forget to register your challenge!
     */

    //this is the name for the database
    //if the name is changed, a new record will be added to the database with the "new" challenge
    public String getName();

    //this is the item used for displaying in Minecraft (with color-codes)
    public ItemStack getItem();





}

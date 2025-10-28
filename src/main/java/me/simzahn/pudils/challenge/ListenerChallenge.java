package me.simzahn.pudils.challenge;

import org.bukkit.event.Listener;

public interface ListenerChallenge extends Challenge {


    //this is a listener challenge, so we need to return a listener
    //this listener is only registered, while the challenge is active and the timer is running
    public Listener getChallengeListener();

}

/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ethan on 2/5/2017.
 */
public class DuelEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private Player winner;
    private Player loser;

    public DuelEndEvent(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public Player getLoser() {
        return loser;
    }

    public Player getWinner() {
        return winner;
    }
}

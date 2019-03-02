/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.event;

import me.borawski.duels.backend.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ethan on 2/5/2017.
 */
public class DuelStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private Player one;
    private Player two;
    private Queue type;

    public DuelStartEvent(Queue gamemode, Player one, Player two) {
        this.one = one;
        this.two = two;
        this.type = gamemode;
    }

    public Player getOne() {
        return one;
    }

    public Player getTwo() {
        return two;
    }

    public Queue getType() {
        return type;
    }
}

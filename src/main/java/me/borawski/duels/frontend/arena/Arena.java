/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.frontend.arena;

import org.bukkit.Location;

/**
 * Created by Ethan on 2/6/2017.
 */
public interface Arena {

    String getName();
    Location getFirst();
    Location getSecond();

}

/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.util;

import me.borawski.duels.Duels;
import me.borawski.duels.frontend.listener.DuelListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 2/12/2017.
 */
public class WinUtil {

    public static void computeNewElo(Player winner, Player loser) {
        int elo = Duels.getInstance().getUserManager().queryOnline(loser.getUniqueId()).getElo();
        int welo = Duels.getInstance().getUserManager().queryOnline(winner.getUniqueId()).getElo();

        // Winner = f(x) = z + 0.5%(x)
        int newWinner = (int) Math.round((welo) + (0.005 * elo));
        Duels.getInstance().getUserManager().queryOnline(winner.getUniqueId()).setElo(newWinner);
        String change = ChatColor.GREEN + "(+" + Math.round((0.005) * elo) + ")";
        DuelListener.getChangeCache().put(winner.getUniqueId(), change);

        // Loser = f(x) = z - 0.5%(x)
        int newLoser = (int) Math.round((elo) - (0.005 * elo));
        Duels.getInstance().getUserManager().queryOnline(loser.getUniqueId()).setElo(newLoser);
        String changeL = ChatColor.RED + "(-" + Math.round((0.005) * elo) + ")";
        DuelListener.getChangeCache().put(loser.getUniqueId(), changeL);
    }

}

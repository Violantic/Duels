/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.util;

import me.borawski.duels.Duels;
import me.borawski.duels.backend.database.OnlineUser;
import me.borawski.duels.listener.DuelListener;
import me.borawski.duels.rating.EloBracket;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 2/12/2017.
 */
public class WinUtil {

    public static void computeNewElo(Player winner, Player loser) {
        OnlineUser w = Duels.getInstance().getUserManager().queryOnline(winner.getUniqueId());
        OnlineUser l = Duels.getInstance().getUserManager().queryOnline(loser.getUniqueId());
        int elo = l.getElo();
        int welo = w.getElo();

        // Winner = f(x) = z + 0.5%(x)
        int newWinner = (int) Math.round((welo) + (0.01 * elo));
        w.setElo(newWinner);
        String change = ChatColor.GREEN + "(+" + Math.round((0.01) * elo) + ")";
        DuelListener.getChangeCache().put(winner.getUniqueId(), change);
        EloBracket winnerCurrent = w.getCurrent();
        w.calculateNewRank();
        if(winnerCurrent != w.getCurrent()) {
            String[] msg = new String[] {
                    "&7&m------------------------------",
                    "&c&lDUELS",
                    "&7Your new rank is " + w.getCurrent().getColor() + w.getCurrent().getName().toUpperCase(),
                    "&7&m------------------------------"
            };

            for(String i : msg) {
                ChatUtil.sendCenteredMessage(winner, i.replace("&", ChatColor.COLOR_CHAR + ""));
            }
            winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 10, 1);
        }

        // Loser = f(x) = z - 0.5%(x)
        int newLoser = (int) Math.round((elo) - (0.01 * elo));
        l.setElo(newLoser);
        Duels.getInstance().getUserManager().queryOnline(loser.getUniqueId()).setElo(newLoser);
        String changeL = ChatColor.RED + "(-" + Math.round((0.01) * elo) + ")";
        DuelListener.getChangeCache().put(loser.getUniqueId(), changeL);
        EloBracket loserCurrent = l.getCurrent();
        l.calculateNewRank();
        if(loserCurrent != l.getCurrent()) {
            String[] msg = new String[] {
                    "&7&m------------------------------",
                    "&c&lDUELS",
                    "&7Your new rank is " + l.getCurrent().getColor() + l.getCurrent().getName().toUpperCase(),
                    "&7&m------------------------------"
            };

            for(String i : msg) {
                ChatUtil.sendCenteredMessage(loser, i.replace("&", ChatColor.COLOR_CHAR + ""));
            }
            loser.playSound(loser.getLocation(), Sound.LEVEL_UP, 10, 1);
        }
    }

}

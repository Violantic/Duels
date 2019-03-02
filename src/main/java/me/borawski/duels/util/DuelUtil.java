/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.util;

import me.borawski.duels.Duels;
import me.borawski.duels.backend.queue.Queue;
import me.borawski.duels.arena.Arena;
import me.borawski.duels.event.DuelStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Ethan on 1/31/2017.
 */
public class DuelUtil {

    public static void initiateDuel(final Queue gameType, final Player one, final Player two) {
        Random random = new Random();
        int index = random.nextInt(Duels.getInstance().getArenaList().size());
        final Arena arena = Duels.getInstance().getArenaList().get(index);
        Duels.getInstance().getQueueHandler().getAvailableArenas().remove(arena.getName());
        Duels.getInstance().getQueueHandler().getCurrentArenas().put(one.getUniqueId(), arena.getName());

        one.sendMessage(("&a&l(!) &r&aA match has been found! Sending you to " + arena.getName() + " in 5 seconds...").replace("&", ChatColor.COLOR_CHAR + ""));
        two.sendMessage(("&a&l(!) &r&aA match has been found! Sending you to " + arena.getName() + " in 5 seconds...").replace("&", ChatColor.COLOR_CHAR + ""));

        new BukkitRunnable() {
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if(player != two) {
                        one.hidePlayer(player);
                    } else if(player != one) {
                        two.hidePlayer(player);
                    }
                }

                one.teleport(arena.getFirst());
                two.teleport(arena.getSecond());

                Duels.getInstance().getQueueHandler().storeAndClearInventory(one);
                Duels.getInstance().getQueueHandler().storeAndClearInventory(two);

                Duels.getInstance().getServer().getPluginManager().callEvent(new DuelStartEvent(gameType, one, two));

                for(ItemStack item : gameType.getType()) {
                    one.getInventory().addItem(item);
                    two.getInventory().addItem(item);
                }

                one.sendMessage("&e&l(!) &r&eFIGHT!".replace("&", ChatColor.COLOR_CHAR + ""));
                two.sendMessage("&e&l(!) &r&eFIGHT!".replace("&", ChatColor.COLOR_CHAR + ""));
            }
        }.runTaskLater(Duels.getInstance(), 5*20L);
    }

    public static boolean isDueling(Player p) {
        return (Duels.getInstance().getQueueHandler().getMatched().containsKey(p.getUniqueId()) || Duels.getInstance().getQueueHandler().getMatched().containsValue(p.getUniqueId()));
    }

}

/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.listener;

import me.borawski.duels.Duels;
import me.borawski.duels.arena.Arena;
import me.borawski.duels.backend.database.OnlineUser;
import me.borawski.duels.backend.queue.QueueHandler;
import me.borawski.duels.event.DuelEndEvent;
import me.borawski.duels.event.DuelStartEvent;
import me.borawski.duels.util.WinUtil;
import me.borawski.duels.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by Ethan on 2/5/2017.
 */
public class DuelListener implements Listener {

    private Duels instance;
    public static Map<UUID, String> changeCache = new HashMap<>();

    public DuelListener(Duels instance) {
        this.instance = instance;
        changeCache = new ConcurrentHashMap<UUID, String>();
    }

    public Duels getInstance() {
        return instance;
    }

    public static Map<UUID, String> getChangeCache() {
        return changeCache;
    }

    @EventHandler
    public void onEnd(final DuelEndEvent event) {
        for(Player player : getInstance().getServer().getOnlinePlayers()) {
            event.getWinner().showPlayer(player);
            event.getLoser().showPlayer(player);
        }

        getInstance().getQueueHandler().finish(event.getWinner());

        WinUtil.computeNewElo(event.getWinner(), event.getLoser());
        getInstance().getUserManager().unregister(event.getWinner().getUniqueId());
        getInstance().getUserManager().unregister(event.getLoser().getUniqueId());

        String[] msg = new String[] {
                "&7&m------------------------------",
                "&c&lDUELS",
                "&a&l" + event.getWinner().getName() + "&r &7has defeated &c&l" + event.getLoser().getName(),
                "&7Your new &c&lELO&r&7 is &c&l{elo}&r&7.",
                "{change}",
                "&7&m------------------------------"
        };

        for(String i : msg) {
            ChatUtil.sendCenteredMessage(event.getWinner(), i.replace("&", ChatColor.COLOR_CHAR + "").replace("{elo}", getInstance().getUserManager().queryOnline(event.getWinner().getUniqueId()).getElo() + "").replace("{change}", getChangeCache().get(event.getWinner().getUniqueId())));
            ChatUtil.sendCenteredMessage(event.getLoser(), i.replace("&", ChatColor.COLOR_CHAR + "").replace("{elo}", getInstance().getUserManager().queryOnline(event.getLoser().getUniqueId()).getElo() + "").replace("{change}", getChangeCache().get(event.getLoser().getUniqueId())));
        }

        getChangeCache().remove(event.getWinner().getUniqueId());
        getChangeCache().remove(event.getLoser().getUniqueId());

        getInstance().getPostWait().add(event.getWinner().getUniqueId());
        getInstance().getPostWait().add(event.getLoser().getUniqueId());

        new BukkitRunnable() {
            public void run() {
//                getInstance().getQueueHandler().restoreInventory(event.getWinner());
//                getInstance().getQueueHandler().restoreInventory(event.getLoser());
                getInstance().getHistory().getBook(event.getWinner().getUniqueId());
                getInstance().getHistory().getBook(event.getLoser().getUniqueId());

                Location location = getInstance().SPAWN;
                event.getWinner().teleport(location);
                event.getLoser().teleport(location);

                OnlineUser winner, loser;
                winner = getInstance().getUserManager().queryOnline(event.getWinner().getUniqueId());
                loser = getInstance().getUserManager().queryOnline(event.getLoser().getUniqueId());
                winner.equipGear();
                loser.equipGear();

                getInstance().getPostWait().remove(event.getWinner().getUniqueId());
                getInstance().getPostWait().remove(event.getLoser().getUniqueId());

                QueueHandler handler = getInstance().getQueueHandler();
                String output = " is now available";
                String arena = "";
                if(handler.getCurrentArenas().containsKey(event.getWinner().getUniqueId())) {
                    arena = handler.getCurrentArenas().get(event.getWinner().getUniqueId());
                } else {
                    arena = handler.getCurrentArenas().get(event.getLoser().getUniqueId());
                }
                handler.getAvailableArenas().add(arena);
                getInstance().getLogger().log(Level.INFO, arena + output);
            }
        }.runTaskLater(getInstance(), 20*5);

        getInstance().getServer().broadcastMessage(event.getWinner().getName() + " has defeated " + event.getLoser().getName() + " in a duel");
    }

    @EventHandler
    public void onStart(DuelStartEvent event) {
        event.getOne().setHealth(20D);
        event.getTwo().setHealth(20D);

        String[] msg = new String[] {
                "&7&m------------------------------",
                "&c&lDUELS",
                "&a[" + event.getType().getName().toUpperCase() + "]",
                "&eYou are entering a 1v1 Duel",
                "&7&m------------------------------"
        };

        for(String i : msg) {
            ChatUtil.sendCenteredMessage(event.getOne(), i.replace("&", ChatColor.COLOR_CHAR + ""));
            ChatUtil.sendCenteredMessage(event.getTwo(), i.replace("&", ChatColor.COLOR_CHAR + ""));
        }
    }
}

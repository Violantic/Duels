/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.frontend.listener;

import me.borawski.duels.Duels;
import me.borawski.duels.backend.util.WinUtil;
import me.borawski.duels.frontend.event.DuelEndEvent;
import me.borawski.duels.frontend.event.DuelStartEvent;
import me.borawski.duels.util.ChatUtil;
import me.borawski.duels.util.ColorEnum;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ethan on 2/5/2017.
 */
public class DuelListener implements Listener {

    private Duels instance;
    public static Map<UUID, String> changeCache;

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

        /**
        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i > 20) {
                    cancel();
                    return;
                }
                i++;

                Firework fw = (Firework) event.getWinner().getWorld().spawnEntity(event.getWinner().getLocation(), EntityType.FIREWORK);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder().withColor(ColorEnum.RANDOM.getFireworkColor()).with(FireworkEffect.Type.BALL).build());
                fw.setFireworkMeta(meta);
                fw.detonate();
            }
        }.runTaskTimer(getInstance(), 0l, 10L);
         **/

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
                getInstance().getQueueHandler().restoreInventory(event.getWinner());
                getInstance().getQueueHandler().restoreInventory(event.getLoser());
                getInstance().getHistory().getBook(event.getWinner().getUniqueId());
                getInstance().getHistory().getBook(event.getLoser().getUniqueId());

                Location location = getInstance().getLocation("world", "187.5,70,492.5,0,0");
                event.getWinner().teleport(location);
                event.getLoser().teleport(location);

                getInstance().getPostWait().remove(event.getWinner().getUniqueId());
                getInstance().getPostWait().remove(event.getLoser().getUniqueId());
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
                "&eYou are entering a 1v1 Duel",
                "&7&m------------------------------"
        };

        for(String i : msg) {
            ChatUtil.sendCenteredMessage(event.getOne(), i.replace("&", ChatColor.COLOR_CHAR + ""));
            ChatUtil.sendCenteredMessage(event.getTwo(), i.replace("&", ChatColor.COLOR_CHAR + ""));
        }
    }
}

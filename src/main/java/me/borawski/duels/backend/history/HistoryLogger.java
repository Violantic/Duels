/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.history;

import me.borawski.duels.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ethan on 2/26/2017.
 */
public class HistoryLogger {

    private Map<UUID, List<String>> log;

    public HistoryLogger() {
        log = new ConcurrentHashMap<UUID, List<String>>();
    }

    public Map<UUID, List<String>> getLog() {
        return log;
    }

    public void setLog(Map<UUID, List<String>> log) {
        this.log = log;
    }

    public void register(UUID player) {
        getLog().put(player, new ArrayList<String>());
        log(player, "Your game history is logged here, enjoy!");
    }

    public void unregister(UUID player) {
        getLog().remove(player);
    }

    public void log(UUID player, String entry) {
        long current = System.currentTimeMillis();
        Date date = new Date(current);
        SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String formatted = f.format(date);

        getLog().get(player).add(ChatColor.RED + "" + ChatColor.BOLD + ">> " + ChatColor.RESET + "" + ChatColor.GRAY + entry + ChatColor.RED + " @ " + formatted);
    }

    public ItemStack getBook(UUID player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setPages(getLog().get(player));
        bm.setAuthor("Dueling Server");
        bm.setDisplayName("Duel History @ " + TimeUtil.getTime());
        book.setItemMeta(bm);
        Bukkit.getPlayer(player).getInventory().addItem(book);
        register(player);

        return book;
    }


}

/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.command;

import me.borawski.duels.Duels;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 2/24/2017.
 */
public class EloCommand implements CommandExecutor {

    private Duels instance;

    public EloCommand(Duels instance) {
        this.instance = instance;
    }

    public Duels getInstance() {
        return instance;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        try {
            commandSender.sendMessage("&c&l>> &r&7Your ELO is [&n{elo}&r&7].".replace("&", ChatColor.COLOR_CHAR + "").replace("{elo}", getInstance().getUserManager().queryOnline(((Player) commandSender).getUniqueId()).getElo() + ""));
            return true;
        } catch (Exception e) {
            commandSender.sendMessage("&c&l>> &r&cError occurred, go yell at Ethan.".replace("&", ChatColor.COLOR_CHAR + ""));
        }
        return false;
    }
}

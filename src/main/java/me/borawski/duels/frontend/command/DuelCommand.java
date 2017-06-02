/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.frontend.command;

import me.borawski.duels.Duels;
import me.borawski.duels.frontend.GUI.custom.TesGUI;
import me.borawski.duels.frontend.arena.Arena;
import me.borawski.duels.util.ChatUtil;
import me.borawski.duels.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Ethan on 2/23/2017.
 */
public class DuelCommand implements CommandExecutor {

    private Duels instance;

    public DuelCommand(Duels instance) {
        this.instance = instance;
    }

    public Duels getInstance() {
        return instance;
    }

    private FileConfiguration getConfig() {
        return getInstance().getConfig();
    }

    public boolean onCommand(final CommandSender commandSender, final Command command, String s, String[] args) {
            if (commandSender instanceof Player) {
                if (args.length == 0) {
                    new TesGUI(getInstance(), (Player) commandSender).show();
                } else if (args.length == 2) {
                    if(!commandSender.isOp()) {
                        commandSender.sendMessage(ChatColor.RED + "You can't edit dueling properties");
                        return false;
                    }

                    if(args[0].equalsIgnoreCase("create")) {
                        final String name = args[1];

                        if(getInstance().duelTypes.containsKey(name)) {
                            commandSender.sendMessage(ChatColor.RED + "That dueling type already exists! If you think otherwise, restart the server and try again.");
                            return false;
                        }

                        getConfig().createSection("duels." + name.toLowerCase());
                        getConfig().getConfigurationSection("duels." + name.toLowerCase()).set("items", new ArrayList<String>());
                        getConfig().getConfigurationSection("duels." + name.toLowerCase()).set("arena", "gladiator");
                        getInstance().saveConfig();

                        String[] msg = new String[]{
                                "&7&m-----------------------------------------------------",
                                "&c&lDUELS",
                                "&7&lYou created a new duel: &c&l" + name,
                                "&7To edit the items of a duel kit: &c/duel edit <kit>",
                                "&dThe duel type will not be effective until restart",
                                "&7&m-----------------------------------------------------",
                        };

                        for (String i : msg) {
                            ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                        }
                    }
                    else if(args[0].equalsIgnoreCase("addhand")) {
                        String name = args[1];
                        if(!getInstance().duelTypes.containsKey(name)) {
                            commandSender.sendMessage(ChatColor.RED + "There is no such dueling type! If you think otherwise, restart the server and try again.");
                            return false;
                        }

                        String toString = ItemUtil.itemToString(((Player) commandSender).getItemInHand());
                        getConfig().getStringList("duels." + name + ".items").add(toString);
                        getInstance().saveConfig();

                        String[] msg = new String[]{
                                "&7&m-----------------------------------------------------",
                                "&c&lDUELS",
                                "&7&lYou added an item to dueling kit: &c&l" + name,
                                "&dThe duel edit will not be effective until restart",
                                "&7&m-----------------------------------------------------",
                        };

                        for (String i : msg) {
                            ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                        }
                    } else if (args[0].equalsIgnoreCase("itemlist")) {
                        String name = args[1];
                        if(!getInstance().duelTypes.containsKey(name)) {
                            commandSender.sendMessage(ChatColor.RED + "There is no such dueling type! If you think otherwise, restart the server and try again.");
                            return false;
                        }

                        commandSender.sendMessage("&c&l>> &r&7This dueling kit has the following items: ".replace("&", ChatColor.COLOR_CHAR + ""));
                        final List<ItemStack> items = getInstance().getQueue(name).getType();
                        items.stream().forEach(new Consumer<ItemStack>() {
                            public void accept(ItemStack itemStack) {
                                commandSender.sendMessage(ChatColor.YELLOW + "[" + items.indexOf(itemStack) + "]: '" + ChatColor.GRAY + ItemUtil.itemToString(itemStack) + ChatColor.YELLOW + "'");
                            }
                        });
                    }
                } else if (args.length == 3) {
                    if(args[0].equalsIgnoreCase("setarena")) {
                        String duel = args[1];
                        String arena = args[2];

                        if(!getInstance().duelTypes.containsKey(duel)) {
                            commandSender.sendMessage(ChatColor.RED + "There is no such dueling type! If you think otherwise, restart the server and try again.");
                            return false;
                        }

                        if(getInstance().getArena(arena) == null) {
                            commandSender.sendMessage(ChatColor.RED + "There is no such arena, the available arenas are:");
                            for(Arena a : getInstance().getArenaList()) {
                                commandSender.sendMessage(ChatColor.RED + "- " + a.getName());
                            }
                            return false;
                        }

                        getConfig().getConfigurationSection("duels." + duel.toLowerCase()).set("arena", arena.toLowerCase());
                        getInstance().saveConfig();

                        String[] msg = new String[]{
                                "&7&m-----------------------------------------------------",
                                "&7&lDUELS",
                                "&7&lYou have edited duel: &c&l" + duel,
                                "&7The new arena of this duel is: &c&l" + arena.toUpperCase(),
                                "&dThe duel edit will not be effective until restart",
                                "&7&m-----------------------------------------------------",
                        };

                        for (String i : msg) {
                            ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                        }
                    } else if(args[0].equalsIgnoreCase("removeitem")) {
                        String duel = args[1];
                        int index = Integer.parseInt(args[2]);

                        if(!getInstance().duelTypes.containsKey(duel)) {
                            commandSender.sendMessage(ChatColor.RED + "There is no such dueling type! If you think otherwise, restart the server and try again.");
                            return false;
                        }

                        try {
                            getConfig().getConfigurationSection("duels." + duel).getStringList("items").remove(index);
                            getInstance().saveConfig();

                            String[] msg = new String[]{
                                    "&7&m-----------------------------------------------------",
                                    "&c&lDUELS",
                                    "&7&lYou removed an item from dueling kit: &c&l" + duel,
                                    "&dThe duel edit will not be effective until restart",
                                    "&7&m-----------------------------------------------------",
                            };

                            for (String i : msg) {
                                ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                            }
                        } catch (Exception e) {
                            commandSender.sendMessage(ChatColor.RED + "That index does not exist! do /duel itemlist <duel> to see all items");
                        }
                    }
                }
            }
            return false;
        }

}

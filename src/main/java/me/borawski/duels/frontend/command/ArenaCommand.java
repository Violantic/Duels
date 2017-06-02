/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.frontend.command;

import me.borawski.duels.Duels;
import me.borawski.duels.frontend.arena.Arena;
import me.borawski.duels.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 2/24/2017.
 */
public class ArenaCommand implements CommandExecutor {

    private Duels instance;

    public ArenaCommand(Duels instance) {
        this.instance = instance;
    }

    public Duels getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return getInstance().getConfig();
    }

    public boolean onCommand(final CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            if (!commandSender.isOp()) {
                commandSender.sendMessage(ChatColor.RED + "You can't alter dueling arenas!");
                return false;
            }

            if (args.length == 1) {
                try {
                    String name = args[0];
                    Arena a = getInstance().getArena(name);

                    String[] msg = new String[]{
                            "&7&m-----------------------------------------------------",
                            "&c&lDUELS",
                            "&c&l" + a.getName().toUpperCase(),
                            "&7&lFirst Location",
                            ("&c&l>> &r&8&lX:&r&e " + a.getFirst().getBlockX() + " &r&8&lY:&r&e " + a.getFirst().getBlockY() + " &r&8&lZ:&r&e " + a.getFirst().getBlockZ()).replace("&", ChatColor.COLOR_CHAR + ""),
                            "&7&lSecond Location",
                            ("&c&l>> &r&8&lX:&r&e " + a.getSecond().getBlockX() + " &r&8&lY:&r&e " + a.getSecond().getBlockY() + " &r&8&lZ:&r&e " + a.getSecond().getBlockZ()).replace("&", ChatColor.COLOR_CHAR + ""),
                            "&7&m-----------------------------------------------------",
                    };

                    for (String i : msg) {
                        ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                    }
                } catch (Exception e) {
                    commandSender.sendMessage("&c&l>> &r&cThat arena does not exist".replace("&", ChatColor.COLOR_CHAR + ""));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    final String name = args[1];
                    getConfig().createSection("arenas." + name);
                    getConfig().getConfigurationSection("arenas." + name).set("first", "0,0,0,0,0");
                    getConfig().getConfigurationSection("arenas." + name).set("second", "0,0,0,0,0");
                    getInstance().saveConfig();

                    getInstance().arenaList.add(new Arena() {
                        public String getName() {
                            return name;
                        }

                        public Location getFirst() {
                            return ((Player) commandSender).getLocation();
                        }

                        public Location getSecond() {
                            return ((Player) commandSender).getLocation();
                        }
                    });

                    String[] msg = new String[]{
                            "&7&m-----------------------------------------------------",
                            "&c&lDUELS",
                            "&7&lYou created a new arena: &c&l" + name,
                            "&7to set the points do &e/arena set <arena> <first|second>",
                            "&7&m-----------------------------------------------------",
                    };

                    for (String i : msg) {
                        ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equals("set")) {
                    String arena = args[1];
                    if (args[2].equalsIgnoreCase("first") || args[2].equalsIgnoreCase("second")) {
                        Location l = ((Player) commandSender).getLocation();
                        getConfig().getConfigurationSection("arenas." + arena).set(args[2], l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch());
                        getInstance().saveConfig();

                        String[] msg = new String[]{
                                "&7&m-----------------------------------------------------",
                                "&c&lDUELS",
                                "&c&l" + arena.toUpperCase(),
                                "&7&lYou updated the &c&l" + args[2] + " &7&llocation",
                                "&7The new location is &c" + Math.round(l.getX()) + "&7, &e" + Math.round(l.getY()) + "&7, &e" + Math.round(l.getZ()) + "&7, &e" + Math.round(l.getYaw()) + "&7, &e" + Math.round(l.getPitch()) + " &7(X,Y,Z,Yaw,Pitch)",
                                "&7&m-----------------------------------------------------",
                        };

                        for (String i : msg) {
                            ChatUtil.sendCenteredMessage((Player) commandSender, i.replace("&", ChatColor.COLOR_CHAR + ""));
                        }

                    }
                }
            }
        }
        return false;
    }
}

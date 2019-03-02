package me.borawski.duels.rating;

import me.borawski.duels.Duels;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public enum EloBracket {

    LEATHER_I("Leather I", ChatColor.DARK_GRAY, 1000),
    LEATHER_II("Leather II", ChatColor.DARK_GRAY, 1050),
    LEATHER_III("Leather III", ChatColor.DARK_GRAY, 1100),
    LEATHER_IV("Leather IV", ChatColor.DARK_GRAY, 1200),

    IRON_I("Iron I", ChatColor.GRAY, 1300),
    IRON_II("Iron II", ChatColor.GRAY, 1400),
    IRON_III("Iron III", ChatColor.GRAY, 1500),
    IRON_IV("Iron IV", ChatColor.GRAY, 1600),

    GOLD_I("Gold I", ChatColor.GOLD, 1700),
    GOLD_II("Gold II", ChatColor.GOLD, 1800),
    GOLD_III("Gold III", ChatColor.GOLD, 1900),
    GOLD_IV("Gold IV", ChatColor.GOLD, 2000),

    DIAMOND_I("Diamond I", ChatColor.AQUA, 2050),
    DIAMOND_II("Diamond II", ChatColor.AQUA, 2100),
    DIAMOND_III("Diamond III", ChatColor.AQUA, 2150),
    DIAMOND_IV("Diamond IV", ChatColor.AQUA, 2200),

    MASTER("Master", ChatColor.RED, 2400);

    private String name;
    private int min;
    private ChatColor color;

    EloBracket(String name, ChatColor color, int min) {
        this.name = name;
        this.color = color;
        this.min = min;

        Team team = Duels.getInstance().getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(name);
        team.setPrefix(color + "[" + name.substring(0, 1).toUpperCase() + name.split(" ")[1] + "] " + ChatColor.RESET);
    }

    public String getName() {
        return name;
    }

    public int getMin() {
        return min;
    }

    public ChatColor getColor() {
        return color;
    }
}

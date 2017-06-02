/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */
package me.borawski.duels.backend.database;

import me.borawski.duels.Duels;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Ethan on 1/29/2017.
 */
public class OnlineUser implements User {

    private UUID uuid;
    private String name;
    private String lastIp;
    private long lastPlayed;
    private Map<String, Integer> statistics;

    public OnlineUser(UUID uuid, String name, String lastIp) {
        this.uuid = uuid;
        this.name = name;
        this.lastIp = lastIp;
        this.lastPlayed = System.currentTimeMillis();
        this.statistics = Duels.getInstance().getDb().getStats(uuid.toString());
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String lastIP() {
        return lastIp;
    }

    public long lastPlayed() {
        return lastPlayed;
    }

    public Map<String, Integer> getStatistics() {
        return statistics;
    }

    /**
     * Getters and setters
     */

    public Integer getKills() {
        return getStatistics().get("kills");
    }

    public void setKills(int i) {
        getStatistics().put("kills", i);
    }

    public void addKill() {
        getStatistics().put("kills", getStatistics().get("kills")+1);
    }

    public Integer getDeaths() {
        return getStatistics().get("deaths");
    }

    public void setDeaths(int i) {
        getStatistics().put("deaths", i);
    }

    public void addDeath() {
        getStatistics().put("deaths", getStatistics().get("deaths") + 1);
    }

    public Integer getElo() {
        return getStatistics().get("elo");
    }

    public void setElo(int i) {
        getStatistics().put("elo", i);
    }

    public void addElo(int elo) {
        getStatistics().put("elo", getStatistics().get("elo")+1);
    }

    public void sendMessage(String msg) {
        try {
            Duels.getInstance().getServer().getPlayer(getUUID()).sendMessage(("&a&l(!) &r&a" + msg).replace("&", ChatColor.COLOR_CHAR + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRawMessage(String msg) {
        try {
            Duels.getInstance().getServer().getPlayer(getUUID()).sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

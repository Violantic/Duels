/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.database;

import me.borawski.duels.Duels;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ethan on 1/29/2017.
 */
public class DuelsDB {

    private Duels instance;

    private String host;
    private String name;
    private String user;
    private String pass;
    private String table;
    private Connection connection;

    public DuelsDB(Duels instance, String host, String table, String name, String user, String pass) {
        try {
            this.instance = instance;
            this.host = host;
            this.table = table;
            this.name = name;
            this.user = user;
            this.pass = pass;

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + getHost(), getUser(), getPass());


            SETUP_TABLE();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Duels getInstance() {
        return instance;
    }

    /**
     * Core SQL
     **/

    public String getHost() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getTable() {
        return table;
    }

    public Connection getConnection() {
        return connection;
    }

    public void SETUP_TABLE() {
        String users = "CREATE TABLE IF NOT EXISTS " + getTable() + "_users(id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(60), name VARCHAR(16), lastIP VARCHAR(60), lastPlayed BIGINT, kills INT(11), deaths INT(11), ilevel INT(11), elo INT(11), PRIMARY KEY(id))";
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(users);
            executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String GET_TABLE() {
        return getTable();
    }

    /**
     * Util
     **/
    public void executeAsync(final PreparedStatement statement) {
        instance.getServer().getScheduler().runTaskAsynchronously(getInstance(),
                new Runnable() {
                    public void run() {
                        try {
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void setStat(String uuid, String name, int value) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + GET_TABLE() + "_users SET " + name + "='" + value + "' WHERE uuid='" + uuid + "'");
            executeAsync(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getStats(String uuid) {
        Map<String, Integer> stats = new ConcurrentHashMap<String, Integer>();
        ResultSet set = query("uuid", uuid);
        try {
            while (set.next()) {
                stats.put("kills", set.getInt("kills"));
                stats.put("deaths", set.getInt("deaths"));
                stats.put("elo", set.getInt("elo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Query for a specific row of data with the modifier, and the value.
     * Example, if you would like to find an entire User row, you would
     * call this method, and on invocation the parameters would be 'uuid', and '{desired uuid you want to find here}'.
     *
     * @param modifier
     * @param desiredTarget
     * @return
     */
    public ResultSet query(String modifier, String desiredTarget) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + GET_TABLE() + "_users WHERE " + modifier + "='" + desiredTarget + "';");
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void computeIfAbsent(String uuid, String name, String lastIp) {
        try {
            ResultSet set = getConnection().prepareStatement("SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "'").executeQuery();
            if (!set.next()) {
                String insert = "INSERT INTO " + getTable() + "_users VALUES (NULL, '" + uuid + "', '" + name + "', '" + lastIp + "', " + System.currentTimeMillis() + ", 0, 0, 0, 1000)";
                PreparedStatement register = getConnection().prepareStatement(insert);
                executeAsync(register);
            } else {
                System.out.println("[DUELS] : computing player data for : " + uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUUID(String name) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE name='" + name + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName(String uuid) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAddress(String uuid) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getString("lastIP");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getLastPlayed(String uuid) {
        String query = "SELECT * FROM " + getTable() + "_users WHERE uuid='" + uuid + "';";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getLong("lastPlayed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0L;
    }

    public int getId(String uuid) {
        String query = "SELECT id FROM " + getTable() + "_users WHERE uuid='" + uuid + "'";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                return set.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

}

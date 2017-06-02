/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels;

import me.borawski.duels.backend.database.DuelsDB;
import me.borawski.duels.backend.database.UserManager;
import me.borawski.duels.backend.history.HistoryLogger;
import me.borawski.duels.backend.queue.Queue;
import me.borawski.duels.backend.queue.QueueHandler;
import me.borawski.duels.frontend.NPCHandler;
import me.borawski.duels.frontend.arena.Arena;
import me.borawski.duels.frontend.command.ArenaCommand;
import me.borawski.duels.frontend.command.DuelCommand;
import me.borawski.duels.frontend.command.EloCommand;
import me.borawski.duels.frontend.listener.DuelListener;
import me.borawski.duels.frontend.listener.PlayerListener;
import me.borawski.duels.util.ItemUtil;
import net.minecraft.server.v1_11_R1.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Ethan on 1/31/2017.
 */
public class Duels extends JavaPlugin {

    private static Duels instance;
    private QueueHandler queueHandler;
    private DuelsDB db;
    private UserManager userManager;
    private HistoryLogger logger;
    private NPCHandler npcHandler;

    /**
     * variables that are access somewhere else idk
     */
    public List<Arena> arenaList;
    public Map<String, Map<String, Object>> duelTypes;

    public List<UUID> postWait;

    @Override
    public void onEnable() {
        instance = this;
        queueHandler = new QueueHandler(this);
        db = new DuelsDB(this, getConfig().getString("host"), getConfig().getString("table"), getConfig().getString("name"), getConfig().getString("user"), getConfig().getString("pass"));
        userManager = new UserManager(db);
        logger = new HistoryLogger();
        npcHandler = new NPCHandler(this);
        npcHandler.setupNPC();
        System.out.println("[DUELS] There are " + npcHandler.getHumanNpc().size() + " NPC's");

        setupArenas();
        setupQueues();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DuelListener(this), this);

        getServer().getScheduler().runTaskTimer(this, queueHandler, 0L, 20L);

        getCommand("elo").setExecutor(new EloCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("summonduelist").setExecutor(new CommandExecutor() {
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                Player player = (Player) commandSender;
                final double x,y,z;
                x = player.getLocation().getX();
                y = player.getLocation().getY();
                z = player.getLocation().getZ();
                npcHandler.getHumanNpc().stream().forEach(new Consumer<EntityLiving>() {
                    public void accept(EntityLiving entityLiving) {
                        entityLiving.setLocation(x, y, z, 0, 0);
                    }
                });
                return false;
            }
        });

        postWait = new ArrayList<UUID>();
    }

    public void onDisable() {

    }

    public static Duels getInstance() {
        return instance;
    }

    public QueueHandler getQueueHandler() {
        return queueHandler;
    }

    public DuelsDB getDb() {
        return db;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public HistoryLogger getHistory() {
        return logger;
    }

    public List<UUID> getPostWait() {
        return postWait;
    }

    public void setupArenas() {
        arenaList = new ArrayList<Arena>();

        for (final String key : getConfig().getConfigurationSection("arenas").getKeys(false)) {
            arenaList.add(new Arena() {
                public String getName() {
                    return key;
                }

                public Location getFirst() {
                    return getLocation("world", getConfig().getString("arenas." + key + ".first"));
                }

                public Location getSecond() {
                    return getLocation("world", getConfig().getString("arenas." + key + ".second"));
                }
            });
            System.out.println("[DUELS] registering arena '" + key + "'");
        }
    }

    public void setupQueues() {

        this.duelTypes = new ConcurrentHashMap<String, Map<String, Object>>() {
            {
                for (final String key : getConfig().getConfigurationSection("duels").getKeys(false)) {
                    final List<String> items = getConfig().getStringList("duels." + key + ".items");
                    final String arena = getConfig().getString("duels." + key + ".arena");
                    put(key, new HashMap<String, Object>() {
                        {
                            put("name", key);
                            put("items", items);
                            put("arena", arena);
                        }
                    });
                }
            }
        };

        duelTypes.values().stream().forEach(new Consumer<Map<String, Object>>() {
            public void accept(Map<String, Object> stringObjectMap) {
                final List<ItemStack> kit = new ArrayList<ItemStack>();
                ((List<String>) stringObjectMap.get("items")).stream().forEach(new Consumer<String>() {
                    public void accept(String s) {
                        kit.add(ItemUtil.stringToItem(s));
                    }
                });
                getQueueHandler().getQueueList().add(new Queue((String) stringObjectMap.get("name"), new String[]{""}, kit));
            }
        });
    }

    public List<Arena> getArenaList() {
        return arenaList;
    }

    public void setArenaList(List<Arena> arenaList) {
        this.arenaList = arenaList;
    }

    public Map<String, Map<String, Object>> getDuelTypes() {
        return duelTypes;
    }

    public void setDuelTypes(Map<String, Map<String, Object>> duelTypes) {
        this.duelTypes = duelTypes;
    }

    public NPCHandler getNpcHandler() {
        return npcHandler;
    }

    /**
     * Waiting lobby for arena
     *
     * @return
     */
    public Arena getArena(final String s) {
        final Arena[] a = {null};
        arenaList.stream().forEach(new Consumer<Arena>() {
            public void accept(Arena arena) {
                if (arena.getName().equalsIgnoreCase(s)) {
                    a[0] = arena;
                }
            }
        });

        return a[0];
    }

    /**
     * Find a queue by id
     * @param s
     * @return
     */
    public Queue getQueue(final String s) {
        final Queue[] q = {null};
        getQueueHandler().getQueueList().stream().forEach(new Consumer<Queue>() {
            public void accept(Queue queue) {
                if(s.equalsIgnoreCase(queue.getName())) {
                    q[0] = queue;
                }
            }
        });

        return q[0];
    }

    /**
     * Parses a location from config string
     *
     * @param world
     * @param path
     * @return
     */
    public Location getLocation(String world, String path) {
        String[] strings = path.split(",");
        double x = Double.parseDouble(strings[0]);
        double y = Double.parseDouble(strings[1]);
        double z = Double.parseDouble(strings[2]);
        float yaw = Float.parseFloat(strings[3]);
        float pitch = Float.parseFloat(strings[4]);

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

}

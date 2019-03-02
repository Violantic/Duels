/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.queue;

import me.borawski.duels.Duels;
import me.borawski.duels.arena.Arena;
import me.borawski.duels.backend.database.OnlineUser;
import me.borawski.duels.util.DuelUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Ethan on 1/31/2017.
 */
public class QueueHandler implements Runnable {

    private Duels instance;
    private List<Queue> queueList;
    private Map<UUID, UUID> matched;
    private Map<UUID, Map<Integer, ItemStack>> contents;
    private Set<String> availableArenas;
    private Map<UUID, String> currentArenas;

    public QueueHandler(Duels instance) {
        this.instance = instance;
        this.queueList = new ArrayList<Queue>();
        this.matched = new ConcurrentHashMap<UUID, UUID>();
        this.contents = new ConcurrentHashMap<UUID, Map<Integer, ItemStack>>();
        this.availableArenas = new HashSet<>();
        this.currentArenas = new ConcurrentHashMap<>();
    }

    public Duels getInstance() {
        return instance;
    }

    public List<Queue> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<Queue> queueList) {
        this.queueList = queueList;
    }

    public Map<UUID, UUID> getMatched() {
        return matched;
    }

    public void setMatched(Map<UUID, UUID> matched) {
        this.matched = matched;
    }

    public Set<String> getAvailableArenas() {
        return availableArenas;
    }

    public Map<UUID, String> getCurrentArenas() {
        return currentArenas;
    }

    public void storeAndClearInventory(final Player player){
        UUID uuid = player.getUniqueId();

        contents.put(uuid, new HashMap<Integer, ItemStack>() {
            {
                for(int i = 0; i < player.getInventory().getSize(); i++) {
                    if(player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() != Material.AIR) {
                        put(i, player.getInventory().getItem(i));
                    }
                }
            }
        });

        player.getInventory().clear();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void restoreInventory(final Player player){
        UUID uuid = player.getUniqueId();
        Map<Integer, ItemStack> contentz = contents.get(uuid);

        player.getInventory().clear();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        if(contentz != null){
            contentz.entrySet().stream().forEach(new Consumer<Map.Entry<Integer, ItemStack>>() {
                public void accept(Map.Entry<Integer, ItemStack> integerItemStackEntry) {
                    player.getInventory().setItem(integerItemStackEntry.getKey(), integerItemStackEntry.getValue());
                }
            });
        } else{
            player.getInventory().clear();
        }
        contents.remove(uuid);
    }

    public boolean isDueling(Player one, Player two) {
        if(getMatched().containsKey(one.getUniqueId())) {
            return (getMatched().get(one.getUniqueId()).equals(two.getUniqueId()));
        } else {
            return (getMatched().get(two.getUniqueId()).equals(one.getUniqueId()));
        }
    }

    public UUID getTarget(final Player one) {
        if(getMatched().containsKey(one.getUniqueId())) {
            return getMatched().get(one.getUniqueId());
        } else {
            final UUID[] key = new UUID[1];
            getMatched().values().stream().forEach(new Consumer<UUID>() {
                public void accept(UUID uuid) {
                    if(getMatched().get(uuid).equals(one.getUniqueId())) {
                        key[0] = uuid;
                    }
                }
            });

            return key[0];
        }
    }

    public void finish(final Player one) {
        if(getMatched().containsKey(one.getUniqueId())) {
            getMatched().remove(one.getUniqueId());
        } else {
            getMatched().values().stream().forEach(new Consumer<UUID>() {
                public void accept(UUID uuid) {
                    if (getMatched().get(uuid) == null) return;
                    if (getMatched().get(uuid).equals(one.getUniqueId())) {
                        getMatched().remove(uuid);
                    }
                }
            });
        }
    }

    public void run() {
        getQueueList().stream().forEach(new Consumer<Queue>() {
            public void accept(Queue queue) {
                if (!queue.canMatch()) return;
                if (availableArenas.size() == 0) return;

                // Sequentially select top 2 players from every queue. //
                // TODO: Filter through queue via ELO //
                final Player one = getInstance().getServer().getPlayer(queue.getTop());
                final OnlineUser player = getInstance().getUserManager().queryOnline(one.getUniqueId());
                queue.remove(queue.getTop());

                OnlineUser first = getInstance().getUserManager().queryOnline(queue.getList().get(0));
                final int[] distance = {Math.abs(first.getElo() - player.getElo())};
                final int[] idx = {0};

                queue.getList().forEach((other) -> {
                    OnlineUser user = getInstance().getUserManager().queryOnline(other);
                    int otherDistance = Math.abs(user.getElo() - player.getElo());
                    if(otherDistance < distance[0]) {
                        idx[0] = queue.getList().indexOf(other);
                        distance[0] = otherDistance;
                    }
                });

                Player two = getInstance().getServer().getPlayer(queue.getList().get(idx[0]));
//                final Player two = getInstance().getServer().getPlayer(queue.getTop());
//                queue.remove(queue.getTop());

                getMatched().put(one.getUniqueId(), two.getUniqueId());
                DuelUtil.initiateDuel(queue, one, two);
            }
        });
    }
}

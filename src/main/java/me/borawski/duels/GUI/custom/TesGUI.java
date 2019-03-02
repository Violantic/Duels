/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.GUI.custom;

import me.borawski.duels.Duels;
import me.borawski.duels.backend.queue.Queue;
import me.borawski.duels.GUI.CustomIS;
import me.borawski.duels.GUI.ItemGUI;
import me.borawski.duels.GUI.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Ethan on 1/21/2017.
 */
public class TesGUI extends ItemGUI {

    private static volatile int[][] gui = {
            {3, 3, 3, 0, 2, 0, 3, 3, 3},
            {3, 1, 1, 1, 1, 1, 1, 1, 3},
            {3, 3, 3, 0, 4, 0, 3, 3, 3},
    };

    private int rows = 3;

    public TesGUI(Duels instance, Player player) {
        super(instance, null, player, 27);
        setGui();
        int i = getInstance().getArenaList().size();
        while(i > 7) {
            i-=7;
            rows++;
        }
    }

    @Override
    public String getName() {
        return "Duels";
    }

    @Override
    public boolean isCloseOnClick() {
        return false;
    }

    @Override
    public void registerItems() {
        final List<Queue> q = getInstance().getQueueHandler().getQueueList();
        int i = 0;
        int p = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 9; y++) {
                if (gui[x][y] == 1) {
                    if (q.size()-1 >= p) {
                        //System.out.println("Currently on the " + p + (((p + "").endsWith("1")) ? "st" : ((p + "").endsWith("2")) ? "nd" : ((p + "").endsWith("3")) ? "rd" : "th") + " item");
                        List<ItemStack> itemList = q.get(p).getType();
                        List<String> items = new ArrayList<String>();
                        for(int d = 0; d < itemList.size(); d++) {
                            items.add(ChatColor.RED + "" + ChatColor.BOLD + "" + "* " + ChatColor.RESET + "" + ChatColor.GRAY + "x" + ChatColor.RED + "" + itemList.get(d).getAmount() + ChatColor.GRAY + " " + itemList.get(d).getType().name());
                        }
                        final int finalP = p;
                        set(i, new MenuItem(new CustomIS().setMaterial(q.get(p).setItem(q.get(p).getType().get(0)).getItem().getType()).setData((short) 1).setName(("&c&l" + q.get(p).getName()).replace("&", ChatColor.COLOR_CHAR + ""))
                                .addLore(ChatColor.RED + "" + ChatColor.BOLD + "" + "* " + ChatColor.RESET + "" + ChatColor.GRAY + "There are " + ChatColor.RED + "" + q.get(p).getList().size() + ChatColor.GRAY + " players in this queue.")
                                .addLore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------------")
                                .addLore(ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.RESET + "" + ChatColor.GRAY + "Random Arena")
                                .addLore(ChatColor.RED + "" + ChatColor.BOLD + "* " + ChatColor.RESET + "" + ChatColor.GRAY + "Expected wait: " + ChatColor.RED + ((q.get(p).getList().size() % 2 == 0) ? q.get(p).getList().size()/2 + "s" : "Waiting for another player"))
                                .addLore(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------------")
                                .addLores(items), new Runnable() {
                            public void run() {
                                final boolean[] status = {true};
                                getInstance().getQueueHandler().getQueueList().stream().forEach(new Consumer<Queue>() {
                                    public void accept(Queue queue) {
                                        if (queue.getList().contains(getPlayer().getUniqueId())) {
                                            getPlayer().sendMessage("&c&l>> &r&cYou are already in a queue! Leave your queue before you join another.".replace("&", ChatColor.COLOR_CHAR + ""));
                                            close();
                                            status[0] = false;
                                        }
                                    }
                                });

                                if (status[0]) {
                                    getInstance().getQueueHandler().getQueueList().get(finalP).add(getPlayer().getUniqueId());
                                    getPlayer().sendMessage("&c&l>> &r&7You have joined the queue, you will soon be matched with an opponent".replace("&", ChatColor.COLOR_CHAR + ""));
                                    close();
                                    new TesGUI(getInstance(), getPlayer()).show();
                                }
                            }
                        }));
                    } else if(q.size() <= p){
                        set(i, new MenuItem(new CustomIS().setMaterial(Material.COAL).setName(ChatColor.RED + "Coming soon!"), new Runnable() {
                            public void run() {
                            }
                        }));
                    }
                    p++;
                } else if (gui[x][y] == 2) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.COMPASS).setName("&c&l>> &r&7Select dueling type!".replace("&", ChatColor.COLOR_CHAR + "")).addLore("&c&l* &r&7Each dueling type is unique".replace("&", ChatColor.COLOR_CHAR + "")).addLore("&c&l* &r&7Click an icon to join the queue".replace("&", ChatColor.COLOR_CHAR + "")), new Runnable() {
                        public void run() {

                        }
                    }));
                } else if (gui[x][y] == 3) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.STAINED_GLASS_PANE).setData(DyeColor.RED.getWoolData()).setName(" "), new Runnable() {
                        public void run() {

                        }
                    }));
                } else if (gui[x][y] == 5) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.COAL).setName(ChatColor.RED + "Coming soon!"), new Runnable() {
                        public void run() {
                        }
                    }));
                } else if (gui[x][y] == 4) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.BARRIER).setName(ChatColor.RED + "Leave queue"), new Runnable() {
                        public void run() {
                            getInstance().getQueueHandler().getQueueList().stream().forEach(new Consumer<Queue>() {
                                public void accept(Queue queue) {
                                    if (!queue.getList().contains(getPlayer().getUniqueId())) {
                                        return;
                                    }

                                    queue.remove(getPlayer().getUniqueId());
                                    close();
                                    getPlayer().sendMessage("&c&l>> &r&7You have left the queue you were in.".replace("&", ChatColor.COLOR_CHAR + ""));
                                }
                            });
                        }
                    }));
                } else {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.STAINED_GLASS_PANE).setData(DyeColor.WHITE.getWoolData()).setName(" "), new Runnable() {
                        public void run() {

                        }
                    }));
                }
                i++;
            }
        }
    }

    public void setGui() {
        gui = new int[][]{
                {3, 3, 3, 0, 2, 0, 3, 3, 3},
                {3, 1, 1, 1, 1, 1, 1, 1, 3},
                {3, 3, 3, 0, 4, 0, 3, 3, 3},
        };
    }

    public int[][] matrix() {
        return gui;
    }

    public int[][] addRow(int[][] a, int pos, int[] num) {
        int[][] result = gui;
        for(int i = 0; i < pos; i++)
            result[i] = a[i];
        result[pos] = num;
        for(int i = pos + 1; i < a.length; i++)
            result[i] = a[i - 1];
        gui = result;
        return result;
    }
}

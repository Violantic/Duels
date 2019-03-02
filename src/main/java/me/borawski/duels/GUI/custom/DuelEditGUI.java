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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Created by Ethan on 2/7/2017.
 */
public class DuelEditGUI extends ItemGUI {

    private Queue queue;

    public DuelEditGUI(Duels instance, Queue queue, ItemGUI parent, Player p) {
        super(instance, parent, p, 54);
        this.queue = queue;
    }

    public Queue getQueue() {
        return queue;
    }

    @Override
    public String getName() {
        return "Editing "; //+ queue.getName();
    }

    @Override
    public boolean isCloseOnClick() {
        return false;
    }

    @Override
    public void registerItems() {
        int i = 0;
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 9; y++) {
                if (matrix()[x][y] >= 1.0 && matrix()[x][y] < 6.0) {
                    String material = "";
                    double scale = 1.0D;
                    if (matrix()[x][y] >= 1.0 && matrix()[x][y] < 2.0) {
                        material += "LEATHER_";
                        scale = 1.0;
                    } else if (matrix()[x][y] >= 2.0 && matrix()[x][y] < 3.0) {
                        material += "CHAINMAIL_";
                        scale = 2.0;
                    } else if (matrix()[x][y] >= 3.0 && matrix()[x][y] < 4.0) {
                        material += "IRON_";
                        scale = 3.0;
                    } else if (matrix()[x][y] >= 4.0 && matrix()[x][y] < 5.0) {
                        material += "GOLD_";
                        scale = 4.0;
                    } else if (matrix()[x][y] >= 5.0 && matrix()[x][y] < 6.0) {
                        material += "DIAMOND_";
                        scale = 5.0;
                    }

                    Material type;
                        type = Material.valueOf(material + solveGear(matrix()[x][y], scale, GearType.ARMOR));

                    set(i, new MenuItem(new CustomIS().setMaterial(type).setName(ChatColor.AQUA + "Click to add to hotbar"), new Runnable() {
                        public void run() {
                            // Add //
                        }
                    }));
                } else if (matrix()[x][y] >= 6 && matrix()[x][y] < 7) {
                    Material type = Material.valueOf(solveGear(matrix()[x][y], 6.0D, GearType.SWORD));
                    set(i, new MenuItem(new CustomIS().setMaterial(type).setName(ChatColor.AQUA + "Click to add to hotbar"), new Runnable() {
                        public void run() {
                            // Add //
                        }
                    }));
                } else if (matrix()[x][y] >= 7 && matrix()[x][y] < 8) {
                    String gear = solveGear(matrix()[x][y], 7.0D, GearType.BOW);
                    ItemStack item;
                    if (gear != null && gear.contains("-")) {
                        String type = gear.split("-")[0];
                        int amt = Integer.parseInt(gear.split("-")[1]);
                        item = new ItemStack(Material.valueOf(type), amt);
                    } else {
                        item = new ItemStack(Material.valueOf(gear));
                    }
                    set(i, new MenuItem(new CustomIS(item).setName(ChatColor.AQUA + "Click to add to hotbar"), new Runnable() {
                        public void run() {
                            // Add //
                        }
                    }));
                } else if (matrix()[x][y] >= 8 && matrix()[x][y] < 9) {
                    Material type = Material.valueOf(solveGear(matrix()[x][y], 8.0D, GearType.FOOD));
                    ItemStack item;
                    if (matrix()[x][y] == 8.3) {
                        item = new ItemStack(type, 1, (byte) 1);
                    } else {
                        item = new ItemStack(type, 1);
                    }

                    set(i, new MenuItem(new CustomIS(item).setName(ChatColor.AQUA + "Click to add to hotbar"), new Runnable() {
                        public void run() {
                            // Add //
                        }
                    }));
                } else if (matrix()[x][y] >= 9) {
                    Potion potion = new Potion(PotionType.valueOf(solveGear(matrix()[x][y], 9.0D, GearType.POTION)));
                    if (matrix()[x][y] == 9.3) {
                        potion.setSplash(true);
                    }

                    set(i, new MenuItem(new CustomIS(potion.toItemStack(1)).setName(ChatColor.AQUA + "Click to add to hotbar"), new Runnable() {
                        public void run() {
                            // Add //
                        }
                    }));
                } else if (matrix()[x][y] == 0.0) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.STAINED_GLASS_PANE).setData(DyeColor.LIGHT_BLUE.getWoolData()), new Runnable() {
                        public void run() {
                            // Nothing //
                        }
                    }));
                } else if (matrix()[x][y] == 11.0) {
                    set(i, new MenuItem(new CustomIS().setMaterial(Material.BARRIER).setName(ChatColor.RED + "Clear the hotbar"), new Runnable() {
                        public void run() {
                            // Clear hotbar //
                        }
                    }));
                } else if (matrix()[x][y] == 12.0) {

                    set(i, new MenuItem(new CustomIS().setMaterial(Material.BOOK_AND_QUILL).setName(ChatColor.YELLOW + "Copy your hotbar to this duels hotbar"), new Runnable() {
                        public void run() {
                            // Copy your hotbar to duel kit //
                        }
                    }));
                }
                i++;
            }
        }
    }

    public enum GearType {
        ARMOR,
        SWORD,
        BOW,
        FOOD,
        POTION
    }

    private String solveGear(double d, double b, GearType type) {
        double g = d - b;
        if (type == GearType.ARMOR) {
            if (g == 0.0) {
                return "HELMET";
            } else if (g == 0.1) {
                return "CHESTPLATE";
            } else if (g == 0.2) {
                return "LEGGINGS";
            } else if (g == 0.3) {
                return "BOOTS";
            } else {
                return "HELMET";
            }
        } else if (type == GearType.SWORD) {
            if (g == 0.0) {
                return "STONE_SWORD";
            } else if (g == 0.1) {
                return "IRON_SWORD";
            } else if (g == 0.2) {
                return "GOLD_SWORD";
            } else if (g == 0.3) {
                return "DIAMOND_SWORD";
            } else {
                return "STONE_SWORD";
            }
        } else if (type == GearType.BOW) {
            if (g == 0.0) {
                return "BOW";
            } else if (g == 0.1) {
                return "ARROW-64";
            } else if (g == 0.2) {
                return "ARROW-32";
            } else if (g == 0.3) {
                return "ARROW-1";
            } else {
                return "BOW";
            }
        } else if (type == GearType.FOOD) {
            if (g == 0.0) {
                return "MUSHROOM_SOUP";
            } else if (g == 0.1) {
                return "COOKED_BEEF";
            } else {
                return "GOLDEN_APPLE";
            }
        } else if (type == GearType.POTION) {
            if (g == 0.0) {
                return "SPEED";
            } else if (g == 0.1) {
                return "INSTANT_HEAL";
            } else if (g == 0.2) {
                return "STRENGTH";
            } else if (g == 0.3) {
                return "POISON";
            } else {
                return "SPEED";
            }
        }

        return null;
    }

    private double[][] matrix() {
        return new double[][]{
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0},
                {1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1, 8.1, 9.1},
                {1.2, 2.2, 3.2, 4.2, 5.2, 6.2, 7.2, 8.2, 9.2},
                {1.3, 2.3, 3.3, 4.3, 5.3, 6.3, 7.3, 8.3, 9.3},
                {11.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 12.0},
                {-1, -1, -1, -1, -1, -1, -1, -1, -1}
        };
    }
}

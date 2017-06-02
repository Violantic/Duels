/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.util.Random;

/**
 * Created by Ethan on 8/29/2016.
 */
public enum ColorEnum {

    AQUA(ChatColor.AQUA, DyeColor.CYAN, Color.AQUA, 2),
    BLACK(ChatColor.BLACK, DyeColor.BLACK, Color.BLACK, 20),
    BLUE(ChatColor.BLUE, DyeColor.LIGHT_BLUE, Color.BLUE, 13),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, Color.FUCHSIA, 10),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, Color.GRAY, 15),
    GREEN(ChatColor.GREEN, DyeColor.GREEN, Color.GREEN, 19),
    LIGHT_GREEN(ChatColor.GREEN, DyeColor.LIME, Color.LIME, 8),
    DARK_RED(ChatColor.DARK_RED, DyeColor.BROWN, Color.MAROON, 5),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, Color.NAVY, 16),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, Color.OLIVE, 17),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, Color.ORANGE, 12),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, Color.PURPLE, 9),
    RED(ChatColor.RED, DyeColor.RED, Color.RED, 6),
    GRAY(ChatColor.GRAY, DyeColor.SILVER, Color.SILVER, 18),
    TEAL(ChatColor.DARK_AQUA, DyeColor.CYAN, Color.TEAL, 1),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, Color.WHITE, 14),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, Color.YELLOW, 3),
    RANDOM(null, null, null, -1);

    private final ChatColor chatColor;
    private final DyeColor dyeColor;
    private final Color fireworkColor;
    private final int potionID;

    ColorEnum(ChatColor chatColor, DyeColor dyeColor, Color fireworkColor, int potionID) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.fireworkColor = fireworkColor;
        this.potionID = potionID;
    }

    public ChatColor getChatColor() {
        return (this.chatColor == null) ?
                this.values()[new Random().nextInt(this.values().length - 1)].getChatColor() : this.chatColor;
    }

    public DyeColor getDyeColor() {
        return (this.dyeColor == null) ?
                this.values()[new Random().nextInt(this.values().length - 1)].getDyeColor() : this.dyeColor;
    }

    public Color getFireworkColor() {
        return (this.fireworkColor == null) ?
                this.values()[new Random().nextInt(this.values().length - 1)].getFireworkColor() : this.fireworkColor;
    }

    public int getPotionID() {
        return (this.potionID <= 0) ?
                this.values()[new Random().nextInt(this.values().length - 1)].getPotionID() : this.potionID;
    }


}

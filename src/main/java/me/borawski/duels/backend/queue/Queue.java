/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.backend.queue;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ethan on 1/31/2017.
 */
public class Queue {

    private String name;
    private String[] description;
    private LinkedList<UUID> list;
    private List<ItemStack> type;
    private ItemStack item;

    public Queue(String name, String[] description, List<ItemStack> kit) {
        this.name = name;
        this.description = description;
        this.list = new LinkedList<UUID>();
        this.type = kit;
        this.item = new ItemStack(Material.DIAMOND_BLOCK, 1);
    }

    public ItemStack getItem() {
        return item;
    }

    public Queue setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public LinkedList<UUID> getList() {
        return list;
    }

    public void setList(LinkedList<UUID> list) {
        this.list = list;
    }

    public List<ItemStack> getType() {
        return type;
    }

    public void setType(List<ItemStack> type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return (list.size() == 0);
    }

    public boolean canMatch() {
        return (list.size() == 2);
    }

    public void add(UUID uuid) {
        list.add(uuid);
    }

    public UUID remove(UUID uuid) {
        UUID player = list.get(0);
        list.remove(0);
        return player;
    }

    public UUID getTop() {
        return list.get(0);
    }

}

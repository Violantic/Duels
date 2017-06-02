/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.frontend;

import com.mojang.authlib.GameProfile;
import me.borawski.duels.Duels;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Ethan on 4/9/2017.
 */
public class NPCHandler {

    private Duels instance;
    private List<EntityPlayer> humanNpc = new ArrayList<EntityPlayer>();

    public NPCHandler(Duels instance) {
        this.instance = instance;
    }

    public Duels getInstance() {
        return instance;
    }

    public List<EntityPlayer> getHumanNpc() {
        return humanNpc;
    }

    public void setupNPC() {

        /**
         * Duelist NPC
         */
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        final EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.fromString("a762f560-4fce-3236-812a-b80efff0b62b"), ChatColor.GRAY + "Duelist"), new PlayerInteractManager(nmsWorld));

        npc.setLocation(175.5, 70, 492.5, 90f, 4.3f);

        Location start = getInstance().getLocation("world", "175.5,70,492.5,90,4,3");
        Location target = getInstance().getLocation("world", "187.5,70,492.5,0,0");

        Location newLocation = getInstance().lookAt(start, target);
        npc.setLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());

        humanNpc.add(npc);

        /**
         * Rest of NPC's
         */
    }
}

/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */
package me.borawski.duels;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        final EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), ChatColor.YELLOW + "" + ChatColor.BOLD + "Click Me"), new PlayerInteractManager(nmsWorld));
        //nmsWorld.addEntity(npc);
        Location duelist = getInstance().NPC;
        npc.setLocation(duelist.getX(), duelist.getY(), duelist.getZ(), duelist.getYaw(), duelist.getPitch());
        humanNpc.add(npc);

        /**
         * Rest of NPC's
         */
    }
}

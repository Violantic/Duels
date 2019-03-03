/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.duels.listener;

import me.borawski.duels.Duels;
import me.borawski.duels.backend.database.OnlineUser;
import me.borawski.duels.event.DuelEndEvent;
import me.borawski.duels.gui.custom.TesGUI;
import me.borawski.duels.rating.EloBracket;
import me.borawski.duels.util.ChatUtil;
import me.borawski.duels.util.DuelUtil;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

/**
 * Created by Ethan on 2/1/2017.
 */
public class PlayerListener implements Listener {

    private Duels instance;

    public PlayerListener(Duels instance) {
        this.instance = instance;
    }

    public Duels getInstance() {
        return instance;
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        getInstance().getUserManager().register(event.getPlayer().getUniqueId(), event.getPlayer().getName(), "");
        getInstance().getHistory().register(event.getPlayer().getUniqueId());
        final EntityPlayer entityLiving = getInstance().getNpcHandler().getHumanNpc().get(0);

        new BukkitRunnable() {
            public void run() {
                PlayerConnection connection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityLiving));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityLiving));
                Location location = getInstance().getLocation("world", getInstance().getConfig().getString("locations.spawn"));
                event.getPlayer().teleport(location);

                OnlineUser user = getInstance().getUserManager().queryOnline(event.getPlayer().getUniqueId());
                Duels.getInstance().getScoreboard().getTeam(user.getCurrent().getName()).addPlayer(event.getPlayer());
                user.equipGear();

                int elo = user.getElo();
                if(elo >= EloBracket.MASTER.getMin()) {
                    getInstance().getServer().getOnlinePlayers().stream().forEach(new Consumer<Player>() {
                        public void accept(Player player) {
                            player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);
                            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "***");
                            getInstance().getServer().broadcastMessage("");
                            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "Legendary player " + event.getPlayer().getName() + " has logged in");
                            getInstance().getServer().broadcastMessage("");
                            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "***");
                        }
                    });
                }
            }
        }.runTaskLater(getInstance(), 5L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Location NPC = getInstance().getNpcHandler().getHumanNpc().get(0).getBukkitEntity().getLocation();
        Location playerLoc = event.getPlayer().getLocation();
        double distance = Math.sqrt(Math.pow(NPC.getX()-playerLoc.getX(), 2)+Math.pow(NPC.getZ()-playerLoc.getZ(), 2));
        if(distance >= 2.5D) return;

        new TesGUI(getInstance(), event.getPlayer()).show();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (getInstance().getPostWait().contains(event.getDamager().getUniqueId())) {
                event.setCancelled(true);
            }
            if (DuelUtil.isDueling((Player) event.getDamager())) {
                Player target = (Player) event.getEntity();
                if (!getInstance().getQueueHandler().isDueling((Player) event.getDamager(), target)) {
                    event.setCancelled(true);
                    event.getEntity().sendMessage("&cYou can't damage other players while in a duel!".replace("&", ChatColor.COLOR_CHAR + ""));
                    return;
                }

                if(target.getHealth()-event.getDamage() < 0.0D) {
                    event.setCancelled(true);
                    target.setHealth(20D);
                    target.setFoodLevel(20);
                    ((Player) event.getDamager()).setHealth(20D);
                    ((Player) event.getDamager()).setFoodLevel(20);

                    target.getInventory().clear();
                    ((Player) event.getDamager()).getInventory().clear();

                    getInstance().getHistory().log(target.getUniqueId(), "You were killed by " + event.getDamager().getName());
                    getInstance().getHistory().log(event.getDamager().getUniqueId(), "You killed " + target.getName());
                    getInstance().getServer().getPluginManager().callEvent(new DuelEndEvent((Player) event.getDamager(), target));
                    return;
                }

                getInstance().getHistory().log(event.getDamager().getUniqueId(), "You damaged " + target.getName() + " for " + ChatColor.RED + (Math.round(event.getFinalDamage())) + ChatColor.GRAY + " health");
                getInstance().getHistory().log(target.getUniqueId(), "You were damaged by " + ((Player) event.getDamager()).getName() + " for " + ChatColor.RED + (Math.round(event.getFinalDamage())) + ChatColor.GRAY + " health");
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        OnlineUser user = getInstance().getUserManager().queryOnline(event.getPlayer().getUniqueId());
        EloBracket bracket = user.getCurrent();
        event.setFormat(bracket.getColor() + "[" + bracket.getName() + "] " + ChatColor.GRAY + event.getPlayer().getName() + ": " + event.getMessage());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getName().equalsIgnoreCase(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click Me")) {
            new TesGUI(getInstance(), event.getPlayer()).show();
        }
    }
}

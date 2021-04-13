package com.minty.metrocore.methods;

import com.minty.metrocore.MetroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import com.minty.metrocore.methods.PlayerStates;

import java.io.*;
import java.time.LocalDateTime;

public class AdminMode{

    private final MetroCore plugin;
    private final PlayerStates playerStates;
    private static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public AdminMode(MetroCore plugin, PlayerStates playerStates){
        this.plugin = plugin;
        this.playerStates = playerStates;
    }

    public void toggleAdminMode(Player player) {
        if(playerStates.getPlayerState(player).equals(PlayerStates.PlayerState.NORMAL))
            enableAdminMode(player);
        else
            disableAdminMode(player);
    }

    public void enableAdminMode(Player player){
        if(playerStates.getPlayerState(player).equals(PlayerStates.PlayerState.ADMIN)){
            for(String message : plugin.getConfig().getStringList("metroadmin.already_active"))
                plugin.sendWithPrefix(player, message);
        }else{
            playerStates.addPlayers(player, PlayerStates.PlayerState.ADMIN);
            File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
            File file = new File(folder, player.getName() + ".txt");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": **SESSION STARTED**");
                bw.newLine();
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("metrocore.admin"))
                    for(String message : plugin.getConfig().getStringList("metroadmin.turn_on.messages"))
                        plugin.sendWithPrefix(player, message.replace("%player%", player.getName()));
            }
            for(String command : plugin.getConfig().getStringList("metroadmin.turn_on.commands"))
                Bukkit.dispatchCommand(sender, command.replace("%player%", player.getName()));
        }
    }

    public void disableAdminMode(Player player){
        if(!(playerStates.getPlayerState(player).equals(PlayerStates.PlayerState.ADMIN))){
            for(String message : plugin.getConfig().getStringList("metroadmin.already_inactive"))
                plugin.sendWithPrefix(player, message);
        }else{
            playerStates.setPlayerState(player, PlayerStates.PlayerState.NORMAL);
            File folder = new File(plugin.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
            File file = new File(folder, player.getName() + ".txt");
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": **SESSION ENDED**");
                bw.newLine();
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("metrocore.admin"))
                    for(String message : plugin.getConfig().getStringList("metroadmin.turn_off.messages"))
                        plugin.sendWithPrefix(player, message.replace("%player%", player.getName()));
            }
            for(String command : plugin.getConfig().getStringList("metroadmin.turn_off.commands"))
                Bukkit.dispatchCommand(sender, command.replace("%player%", player.getName()));
        }
    }
}

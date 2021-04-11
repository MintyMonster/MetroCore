package com.minty.metrocore.methods;

import com.minty.metrocore.MetroCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.LocalDateTime;

public class AdminMode {

    private final MetroCore plugin;
    private static ConsoleCommandSender sender = Bukkit.getConsoleSender();

    public AdminMode(MetroCore plugin){
        this.plugin = plugin;
    }

    public void toggleAdminMode(Player player) {
        if (plugin.Admin.get(player).equals(true)) {
            disableAdminMode(player);
        } else {
            enableAdminMode(player);
        }
    }

    public void enableAdminMode(Player player) {
        if (plugin.Admin.get(player).equals(true)) {
            for (String s : plugin.getConfig().getStringList("metroadmin.already_active")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            plugin.Admin.replace(player, true);
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("metrocore.admin")) {
                    for (String s : plugin.getConfig().getStringList("metroadmin.turn_on.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replace("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : plugin.getConfig().getStringList("metroadmin.turn_on.commands")) {
                String cmd = s.replace("%player%", player.getName());
                Bukkit.dispatchCommand(sender, cmd);
            }
        }
    }

    public void disableAdminMode(Player player) {
        if (plugin.Admin.get(player).equals(false)) {
            for (String s : plugin.getConfig().getStringList("metroadmin.already_inactive")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            plugin.Admin.replace(player, false);
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
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("metrocore.admin")) {
                    for (String s : plugin.getConfig().getStringList("metroadmin.turn_off.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replace("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : plugin.getConfig().getStringList("metroadmin.turn_off.commands")) {
                String cmd = s.replace("%player%", player.getName());
                Bukkit.dispatchCommand(sender, cmd);
            }

        }
    }
}

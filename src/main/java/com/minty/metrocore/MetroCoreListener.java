package com.minty.metrocore;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.perms.Relation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.awt.*;
import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MetroCoreListener implements Listener {
    Random rnd = new Random();

    @EventHandler
    public void getClaimKey(LandClaimEvent event) {
        List<String> commands = new ArrayList<String>();
        if (!Board.getInstance().getFactionAt(event.getLocation()).isSafeZone()) {
            if (!Board.getInstance().getFactionAt(event.getLocation()).isWarZone()) {
                if (!Board.getInstance().getFactionAt(event.getLocation()).isWilderness()) {
                    if (MetroCore.instance.getConfig().getString("metroclaimkeys.commands.multiple_commands").equalsIgnoreCase("true")) {
                        for (String s : MetroCore.instance.getConfig().getStringList("metroclaimkeys.commands.commands")) {
                            String command = s;
                            String replaced = command.replace("%player%", event.getfPlayer().getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaced);
                        }
                    } else {
                        for (String s : MetroCore.instance.getConfig().getStringList("metroclaimkeys.commands.command")) {
                            commands.add(s);
                        }
                        String command = commands.get(rnd.nextInt(commands.size()));
                        String replaced = command.replace("%player%", event.getfPlayer().getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaced);
                    }
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event){

        Player player = event.getPlayer();

        if(!event.getFrom().getChunk().equals(event.getTo().getChunk())){
            if(player.isFlying()){

                FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
                Location location = player.getLocation();
                FLocation flocation = new FLocation(location);
                Faction faction = Board.getInstance().getFactionAt(flocation);
                Relation allies = faction.getRelationTo(faction);

                if(!(MetroCore.instance.Mod.containsKey(player)) || !(MetroCore.instance.Admin.containsKey(player))){
                    if(!player.isOp()){
                        if(player.getGameMode() != GameMode.CREATIVE){
                            if((faction.isWilderness()) || (faction.isWarZone()) || (faction.isSafeZone())){
                                if(!(allies.isTruce()) || !(allies.isAlly()) || !(allies.isMember())){
                                    if(MetroCore.instance.getConfig().getString("metrofly.messages.disableflight") != null){
                                        try{
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', MetroCore.instance.getConfig().getString("metrofly.messages.disableflight")));
                                            player.setAllowFlight(false);
                                            fplayer.setFlying(false);
                                            for(String s : MetroCore.instance.getConfig().getStringList("metrofly.commands.out_of_claim")){
                                                String c = s.replaceAll("%player%", player.getName());
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                            }

                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void createAdminFile(Player player){
        File dir = MetroCore.instance.getDataFolder();
        File Folder = new File(dir + File.separator + "Logs" + File.separator + "Admin" + File.separator);
        if(!Folder.exists()) Folder.mkdir();
        File file = new File(Folder, player.getName() + ".txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createModFile(Player player){
        File dir = MetroCore.instance.getDataFolder();
        File Folder = new File(dir + File.separator + "Logs" + File.separator + "Mod" + File.separator);
        if(!Folder.exists()) Folder.mkdir();
        File file = new File(Folder, player.getName() + ".txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void logAdminCommands(Player player, String cmd){
        File folder = new File(MetroCore.instance.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
        File file = new File(folder, player.getName() + ".txt");
        if(!file.exists()){
            createAdminFile(player);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": " + cmd);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logModCommands(Player player, String cmd){
        File folder = new File(MetroCore.instance.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
        File file = new File(folder, player.getName() + ".txt");
        if(!file.exists()) {
            createModFile(player);
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("[" + LocalDateTime.now() + "] " + player.getName() + ": " + cmd);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String name = event.getPlayer().getName();
        String UUID = event.getPlayer().getUniqueId().toString();
        String IP = event.getPlayer().getAddress().getAddress().getHostAddress();
        String date = LocalDateTime.now().toString();
        File dir = MetroCore.instance.getDataFolder();
        File playerDataFolder = new File(dir + File.separator + "PlayerData" + File.separator);
        File playerData = new File(playerDataFolder, "PlayerData.db");
        String path = playerData.getPath();
        int rowCount = 0;
        try{
            MetroCore.c = DriverManager.getConnection("jdbc:sqlite:" + path);
            MetroCore.stmt = MetroCore.c.createStatement();
            ResultSet rs = MetroCore.stmt.executeQuery("SELECT * FROM PLAYERDATA");
            rs = MetroCore.stmt.executeQuery("SELECT COUNT(*) FROM PLAYERDATA");
            rs.next();
            rowCount = rs.getInt(1) + 1;
            rs.close();

            ResultSet rsn;
            rsn = MetroCore.stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERDATA WHERE NAME = '" + name + "'");
            rsn.next();
            int playerNum = rsn.getInt("ROWCOUNT");
            rsn.close();

            if(playerNum != 1){
                String sql = "INSERT INTO PLAYERDATA (ID,NAME,UUID,IP,DATE) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + UUID + "', '" + IP + "', '" + date + "');";
                MetroCore.stmt.executeUpdate(sql);
                MetroCore.stmt.close();
                MetroCore.c.close();
                MetroCore.instance.getLogger().info("[MetroCore] Added " + name + " to PlayerData");
            }

            if(event.getPlayer().hasPermission("metrocore.admin")){
                if(!MetroCore.instance.Admin.containsKey(event.getPlayer())){
                    MetroCore.instance.Admin.put(event.getPlayer(), false);
                    createAdminFile(event.getPlayer());
                }
            }else if(event.getPlayer().hasPermission("metrocore.mod")){
                if(!MetroCore.instance.Mod.containsKey(event.getPlayer())){
                    MetroCore.instance.Mod.put(event.getPlayer(), false);
                    createModFile(event.getPlayer());
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int previous = 0;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(MetroCore.instance.Admin.get(event.getPlayer())){
                MetroCore.instance.disableModMode(event.getPlayer());
                MetroCore.instance.Admin.replace(event.getPlayer(), false);
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(MetroCore.instance.Mod.get(event.getPlayer())){
                MetroCore.instance.disableAdminMode(event.getPlayer());
                MetroCore.instance.Mod.replace(event.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(!MetroCore.instance.Admin.containsKey(event.getPlayer())){
                MetroCore.instance.Admin.put(event.getPlayer(), false);
            }
            if(MetroCore.instance.Admin.get(event.getPlayer())){
                logAdminCommands(event.getPlayer(), event.getMessage());
            }

        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(!MetroCore.instance.Mod.containsKey(event.getPlayer())){
                MetroCore.instance.Mod.put(event.getPlayer(), false);
            }
            if(MetroCore.instance.Mod.get(event.getPlayer())){
                logModCommands(event.getPlayer(), event.getMessage());
            }
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if(event.getPlayer().hasPermission("metrocore.admin")){
            if(MetroCore.instance.Admin.get(event.getPlayer())){
                logAdminCommands(event.getPlayer(), event.getMessage());
            }
        }else if(event.getPlayer().hasPermission("metrocore.mod")){
            if(MetroCore.instance.Mod.get(event.getPlayer())){
                logModCommands(event.getPlayer(), event.getMessage());
            }
        }
    }
}

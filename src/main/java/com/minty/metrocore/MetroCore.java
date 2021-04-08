package com.minty.metrocore;

import com.google.gson.JsonParser;
import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.Relation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gjt.mm.mysql.Driver;

import javax.security.auth.login.LoginException;
import java.sql.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class MetroCore extends JavaPlugin {

    public static Connection c = null;
    public static Statement stmt = null;
    public static MetroCore instance;
    HashMap<Player, Boolean> Mod;
    HashMap<Player, Boolean> Admin;
    List<Material> rtpblocks = new ArrayList<>();
    JDA jda;
    // /yeet remake huehuehue
    // DiscordBot from server > bugreports || punishments  || player reports || ranks++ (Discord -> Minecraft <- Sync) || suggestions
    // /punish > GUI with selective options > Follows guidelines > Takes previous offenses into account too
    // PlayerCommandPreprocessEvent > Register commands
    // coinflip command
    // force commands to look for player

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MetroCoreListener(), this);
        try {
            jda = JDABuilder.createDefault("token").build();
            jda.addEventListener(new DiscordListener());
            jda.awaitReady();
            this.getLogger().info("[MetroCore] Connected to Discord.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            this.getLogger().severe("[MetroCore] Cannot connect to Discord");
        }

        getLogger().info("Enabled");
        instance = this;
        Mod = new HashMap<>();
        Admin = new HashMap<>();
        rtpblocks.add(Material.LAVA);
        rtpblocks.add(Material.WATER);
        rtpblocks.add(Material.BIRCH_LEAVES);
        rtpblocks.add(Material.DARK_OAK_LEAVES);
        rtpblocks.add(Material.JUNGLE_LEAVES);
        rtpblocks.add(Material.OAK_LEAVES);
        rtpblocks.add(Material.SPRUCE_LEAVES);
        this.saveDefaultConfig();
        File dir = this.getDataFolder();
        File Folder = new File(dir + File.separator + "Logs" + File.separator);
        File aidanFolder = new File(dir + File.separator + "AidanNotes" + File.separator);
        File bugFolder = new File(dir + File.separator + "BugReports" + File.separator);
        File reportFolder = new File(dir + File.separator + "PlayerReports" + File.separator);
        File playerDataFolder = new File(dir + File.separator + "PlayerData" + File.separator);
        if (!playerDataFolder.exists()) playerDataFolder.mkdir();
        if (!bugFolder.exists()) bugFolder.mkdir();
        if (!aidanFolder.exists()) aidanFolder.mkdir();
        if (!Folder.exists()) Folder.mkdir();
        if (!reportFolder.exists()) reportFolder.mkdir();

        File playerData = new File(playerDataFolder, "PlayerData.db");
        if (!playerData.exists()) {
            try {
                getServer().getLogger().info("[MetroCore] Missing PlayerData database, creating new database.");
                playerData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            String path = playerData.getPath();
            c = DriverManager.getConnection("jdbc:sqlite:" + path);

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS PLAYERDATA (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, UUID TEXT NOT NULL, IP TEXT NOT NULL, DATE TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            getServer().getLogger().info("[MetroCore] Finished creating new PlayerData database");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getLogger().severe("[MetroCore] PlayerData Database not created, please contact Triobros");
        }

        File file = new File(aidanFolder, "AidanNotes.db");
        if (!file.exists()) {
            try {
                getServer().getLogger().info("[MetroCore] Missing AidanNotes database, creating new database.");
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            String path = file.getPath();
            c = DriverManager.getConnection("jdbc:sqlite:" + path);

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS AIDANNOTES (ID INTEGER PRIMARY KEY NOT NULL, NOTE TEXT NOT NULL, STATUS TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            getServer().getLogger().info("[MetroCore] Finished creating new AidanNotes database");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getLogger().severe("[MetroCore] AidanNotes Database not created, please contact Triobros");
        }

        File bugJson = new File(bugFolder, "BugReports.db");
        if (!bugJson.exists()) {
            try {
                getServer().getLogger().info("[MetroCore] Missing BugReports database, creating new database.");
                bugJson.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            String path = bugJson.getPath();
            c = DriverManager.getConnection("jdbc:sqlite:" + path);

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS BUGREPORTS (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, REPORT TEXT NOT NULL, STATUS TEXT NOT NULL)";

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            getServer().getLogger().info("[MetroCore] Finished creating new BugReports database");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getLogger().severe("[MetroCore] BugReports Database not created, please contact Triobros");
        }

        File reportDb = new File(reportFolder, "PlayerReports.db");
        if (!reportDb.exists()) {
            try {
                getServer().getLogger().info("[MetroCore] Missing PlayerReports database, creating new database");
                reportDb.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            String path = reportDb.getPath();
            c = DriverManager.getConnection("jdbc:sqlite:" + path);

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS PLAYERREPORTS (ID INTEGER PRIMARY KEY NOT NULL, PLAYER TEXT NOT NULL, REPORTED TEXT NOT NULL, REASON TEXT NOT NULL, DATE TEXT NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            getServer().getLogger().info("[MetroCore] Finished creating new PlayerReports database");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getLogger().severe("[MetroCore] PlayerReports Database not created, please contact Triobros");
        }
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }


    public void toggleModMode(Player player) {
        if (this.Mod.get(player)) {
            disableModMode(player);
        } else {
            enableModMode(player);
        }
    }

    public void enableModMode(Player player) {
        if (this.Mod.get(player).equals(true)) {
            for (String s : this.getConfig().getStringList("metromoderation.already_active")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            this.Mod.replace(player, true);
            File folder = new File(this.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
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
                if (p.hasPermission("metrocore.mod")) {
                    for (String s : this.getConfig().getStringList("metromoderation.turn_on.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", p.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : this.getConfig().getStringList("metromoderation.turn_on.commands")) {
                String cmd = s.replaceAll("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }
    }

    public void disableModMode(Player player) {
        if (this.Mod.get(player).equals(false)) {
            for (String s : this.getConfig().getStringList("metromoderation.already_inactive")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            this.Mod.replace(player, false);
            File folder = new File(this.getDataFolder() + File.separator + "Logs" + File.separator + "Mod" + File.separator);
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
                if (p.hasPermission("metrocore.mod")) {
                    for (String s : this.getConfig().getStringList("metromoderation.turn_off.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : this.getConfig().getStringList("metromoderation.turn_off.commands")) {
                String cmd = s.replaceAll("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

        }
    }

    public void toggleAdminMode(Player player) {
        if (this.Admin.get(player).equals(true)) {
            disableAdminMode(player);
        } else {
            enableAdminMode(player);
        }
    }

    public void enableAdminMode(Player player) {
        if (this.Admin.get(player).equals(true)) {
            for (String s : this.getConfig().getStringList("metroadmin.already_active")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            this.Admin.replace(player, true);
            File folder = new File(this.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
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
                    for (String s : this.getConfig().getStringList("metroadmin.turn_on.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replace("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : this.getConfig().getStringList("metroadmin.turn_on.commands")) {
                String cmd = s.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }
    }

    public void disableAdminMode(Player player) {
        if (this.Admin.get(player).equals(false)) {
            for (String s : this.getConfig().getStringList("metroadmin.already_inactive")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            this.Admin.replace(player, false);
            File folder = new File(this.getDataFolder() + File.separator + "Logs" + File.separator + "Admin" + File.separator);
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
                    for (String s : this.getConfig().getStringList("metroadmin.turn_off.messages")) {
                        String message = ChatColor.translateAlternateColorCodes('&', s).replace("%player%", player.getName());
                        p.sendMessage(message);
                    }
                }
            }
            for (String s : this.getConfig().getStringList("metroadmin.turn_off.commands")) {
                String cmd = s.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (command.getName().equalsIgnoreCase("fly")) {

            Player player = (Player) sender;
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            Location location = player.getLocation();
            FLocation flocation = new FLocation(location);
            Faction faction = Board.getInstance().getFactionAt(flocation);
            Faction playerFaction = fplayer.getFaction();
            Relation allies = faction.getRelationTo(faction);


            if (!player.isOp() && player.getGameMode() != GameMode.CREATIVE) {
                if (!faction.isWarZone() && !faction.isSafeZone()) {
                    if (allies.isAlly()) {
                        if (sender.hasPermission("metrocore.fly.ally")) {
                            player.setAllowFlight(!player.getAllowFlight());
                            if (player.getAllowFlight()) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.enable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_ally_enable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.disable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_ally_disable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                        }
                    } else if (allies.isTruce()) {
                        if (sender.hasPermission("metrocore.fly.truce")) {
                            player.setAllowFlight(!player.getAllowFlight());
                            if (player.getAllowFlight()) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.enable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_truce_enable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.disable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_truce_disable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                        }
                    } else if (allies.isMember()) {
                        if (sender.hasPermission("metrocore.fly")) {
                            player.setAllowFlight(!player.getAllowFlight());
                            if (player.getAllowFlight()) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.enable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_own_enable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.disable").toString()));
                                for (String s : this.getConfig().getStringList("metrofly.commands.fly_own_disable")) {
                                    String c = s.replaceAll("%player%", sender.getName());
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
                        }
                    } else {
                        String m = MetroCore.instance.getConfig().getString("metrofly.messages.outofclaim");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
                    }
                }
            } else {
                player.setAllowFlight(!player.getAllowFlight());
                if (player.getAllowFlight()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.enable").toString()));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrofly.messages.disable").toString()));
                }
            }

        } else if (command.getName().equalsIgnoreCase("metrocore")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /metrocore [command]");
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("metrocore.admin")) {
                    try {
                        this.reloadConfig();
                        sender.sendMessage(ChatColor.GOLD + "[MetroCore] " + ChatColor.GREEN + "Reload complete.");
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.GOLD + "[MetroCore] " + ChatColor.RED + "Something went wrong whilst reloading config.\nCheck logs for error.");
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        } else if (command.getName().equalsIgnoreCase("mod")) {
            if (this.Mod.containsKey((Player) sender)) {
                if (sender.hasPermission("metrocore.mod")) {
                    if (args.length == 0) {
                        toggleModMode((Player) sender);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("on")) {
                        enableModMode((Player) sender);
                        return true;

                    } else if (args[0].equalsIgnoreCase("off")) {
                        disableModMode((Player) sender);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error! <ModMode> Please try and relog!");
                return true;
            }

        } else if (command.getName().equalsIgnoreCase("admin")) {
            if (this.Admin.containsKey((Player) sender)) {
                if (sender.hasPermission("metrocore.admin")) {
                    if (args.length == 0) {
                        toggleAdminMode((Player) sender);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("on")) {
                        enableAdminMode((Player) sender);
                        return true;
                    } else if (args[0].equalsIgnoreCase("off")) {
                        disableAdminMode((Player) sender);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error! <AdminMode> Please try and relog!");
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("notes")) {
            if (sender.hasPermission("metrocore.notes")) {
                if (args.length == 0) {
                    File dir = this.getDataFolder();
                    File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
                    File file = new File(Folder, "AidanNotes.db");
                    String path = file.getPath();
                    try {
                        c = DriverManager.getConnection("jdbc:sqlite:" + path);
                        stmt = c.createStatement();

                        sender.sendMessage(ChatColor.GOLD + "------ Aidan's notes ------");
                        ResultSet rs = stmt.executeQuery("SELECT ID, NOTE, STATUS FROM AIDANNOTES WHERE STATUS = 'In progress'");
                        while (rs.next()) {
                            int id = rs.getInt("ID");
                            String note = rs.getString("NOTE");
                            sender.sendMessage(ChatColor.GRAY + "#" + String.valueOf(id) + ": " + ChatColor.GREEN + note);
                        }
                        return true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args[1] != null) {
                        File dir = this.getDataFolder();
                        File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
                        File file = new File(Folder, "AidanNotes.db");
                        String path = file.getPath();
                        try {
                            c = DriverManager.getConnection("jdbc:sqlite:" + path);
                            stmt = c.createStatement();
                            String sql = "UPDATE AIDANNOTES SET STATUS = 'Done' WHERE ID = " + args[1].toString();
                            stmt.executeUpdate(sql);
                            stmt.close();
                            c.close();

                            sender.sendMessage(ChatColor.GREEN + "Note " + ChatColor.YELLOW + "[" + args[1].toString() + "]" + ChatColor.GREEN + " removed!");
                            return true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/notes remove <id>]");
                    }
                } else if (args[0].equalsIgnoreCase("add")) {
                    File dir = this.getDataFolder();
                    File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
                    File file = new File(Folder, "AidanNotes.db");
                    String path = file.getPath();
                    if (args.length >= 2) {
                        try {
                            c = DriverManager.getConnection("jdbc:sqlite:" + path);
                            stmt = c.createStatement();
                            ResultSet rc = stmt.executeQuery("SELECT * FROM AIDANNOTES");
                            rc = stmt.executeQuery("SELECT COUNT(*) FROM AIDANNOTES");
                            rc.next();
                            int RowCount = rc.getInt(1) + 1;
                            rc.close();

                            String message = "";
                            for (int i = 1; i < args.length; i++) {
                                message += args[i] + " ";
                            }

                            String message2 = message.replace("'", "");

                            String sql = "INSERT INTO AIDANNOTES (ID,NOTE,STATUS) VALUES (" + String.valueOf(RowCount) + ", '" + message2 + "', 'In progress');";
                            stmt.executeUpdate(sql);
                            stmt.close();
                            c.close();
                            sender.sendMessage(ChatColor.GREEN + "Note added!");

                            Long guild = this.getConfig().getLong("discord.guild_id");
                            Long channelid = this.getConfig().getLong("discord.notes_channel_id");
                            java.awt.Color c = new java.awt.Color(255, 255, 0);
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle(sender.getName() + " added a new note!");
                            eb.setDescription(message);
                            eb.setColor(c);

                            TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                            ch.sendMessage(eb.build()).queue();
                            return true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/notes add <text>]");
                    }
                }
            }

        } else if (command.getName().equalsIgnoreCase("rtp")) {
            if (sender.hasPermission("metrocore.rtp")) {
                Random rnd = new Random();
                Player player = (Player) sender;
                World world = player.getWorld();
                int min = this.getConfig().getInt("metrortp.minimum");
                int max = this.getConfig().getInt("metrortp.maximum");

                boolean isSafe = false;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrortp.messages.looking_for_safe")));
                while (!isSafe) {

                    int x = (int) (Math.random() * max * 2) - min;
                    int z = (int) (Math.random() * max * 2) - min;
                    int y = world.getHighestBlockYAt(x, z);
                    Block b = world.getBlockAt(x, y, z);

                    if (!rtpblocks.contains(b.getType())) {
                        // check for leaves, tree etc (other annoying things)
                        // If player moves or gets attacked, cancel
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("metrortp.messages.found_safe")));
                        Location loc = new Location(player.getWorld(), x, y + 2, z);
                        player.teleport(loc);
                        for (String s : this.getConfig().getStringList("metrortp.commands")) {
                            String cmd = s.replaceAll("%x%", String.valueOf(x)).replaceAll("%y%", String.valueOf(y)).replaceAll("%z%", String.valueOf(z)).replaceAll("%player%", player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        }

                        isSafe = true;
                    }

                }
            }
        } else if (command.getName().equalsIgnoreCase("bugreport")) {
            if (sender.hasPermission("metrocore.bugreport")) {
                File dir = this.getDataFolder();
                File bugFolder = new File(dir + File.separator + "BugReports" + File.separator);
                File bugJson = new File(bugFolder, "BugReports.db");
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("bugreport.error")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                        try {
                            String path = bugJson.getPath();
                            c = DriverManager.getConnection("jdbc:sqlite:" + path);
                            stmt = c.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT ID, NAME, REPORT, STATUS FROM BUGREPORTS");
                            while (rs.next()) {
                                int id = rs.getInt("ID");
                                String name = rs.getString("NAME");
                                String report = rs.getString("REPORT");
                                String result = rs.getString("STATUS");
                                if (result.equalsIgnoreCase("fixed")) {
                                    String message = this.getConfig().getString("bugreport.list_formatting").replaceAll("%id%", String.valueOf(id)).replaceAll("%name%", name).replaceAll("%report%", report);
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.GREEN + " [Fixed]");
                                } else {
                                    String message = this.getConfig().getString("bugreport.list_formatting").replaceAll("%id%", String.valueOf(id)).replaceAll("%name%", name).replaceAll("%report%", report);
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.RED + " [" + result + "]");
                                }


                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Permission denied");
                    }


                } else if (args[0].equalsIgnoreCase("submit")) {
                    if (args.length >= 2) {
                        if (sender.hasPermission("metrocore.bugreport")) {
                            String name = sender.getName();
                            String report = "";
                            for (int i = 1; i < args.length; i++) {
                                report += args[i] + " ";
                            }

                            String report2 = report.replace("'", "''");

                            try {
                                String path = bugJson.getPath();
                                c = DriverManager.getConnection("jdbc:sqlite:" + path);
                                stmt = c.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM BUGREPORTS");
                                rs = stmt.executeQuery("SELECT COUNT(*) FROM BUGREPORTS");
                                rs.next();
                                int rowCount = rs.getInt(1) + 1;
                                rs.close();
                                String sql = "INSERT INTO BUGREPORTS (ID,NAME,REPORT,STATUS) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + report2 + "', 'In progress' );";
                                stmt.executeUpdate(sql);
                                stmt.close();
                                c.close();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("bugreport.report_submitted")));
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if ((p.hasPermission("metrocore.admin")) || (p.hasPermission("metrocore.mod"))) {
                                        String msg = this.getConfig().getString("bugreport.report_submitted_staff").replaceAll("%player%", sender.getName());
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                                    }
                                }

                                Long guild = this.getConfig().getLong("discord.guild_id");
                                Long channelid = this.getConfig().getLong("discord.bugreport_channel_id");
                                java.awt.Color c = new java.awt.Color(255, 0, 0);
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Bug Report: #" + rowCount);
                                eb.setDescription("**ID:** #" + rowCount + "\n" + "**From:** " + sender.getName() + "\n" + "**Description:** " + report);
                                eb.setColor(c);

                                TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                                ch.sendMessage(eb.build()).queue();

                                return true;
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("bugreport.database_error")));
                            }
                        }
                    }
                } else if ((args[0].equalsIgnoreCase("fixed")) || args[0].equalsIgnoreCase("fix")) {
                    if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                        if (args[1] != null) {
                            try {
                                String path = bugJson.getPath();
                                c = DriverManager.getConnection("jdbc:sqlite:" + path);
                                stmt = c.createStatement();

                                String sql = "UPDATE BUGREPORTS SET STATUS = 'Fixed' WHERE ID = " + args[1].toString();
                                stmt.executeUpdate(sql);
                                stmt.close();
                                c.close();
                                String message = this.getConfig().getString("bugreport.bugreport_fixed").replaceAll("%id%", args[1].toString());
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                                Long guild = this.getConfig().getLong("discord.guild_id");
                                Long channelid = this.getConfig().getLong("discord.bugreport_channel_id");
                                java.awt.Color c = new java.awt.Color(0, 200, 255);
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Bug Report: #" + args[1]);
                                eb.setDescription("Marked as **[Fixed]**");
                                eb.setColor(c);

                                TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                                ch.sendMessage(eb.build()).queue();

                                return true;

                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please provide an id to fix problem " + ChatColor.YELLOW + "[/bugreport fixed [id] ]" + ChatColor.RED + "\nUse " + ChatColor.YELLOW + "[/bugreport list]" + ChatColor.RED + "to see ids");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Permission denied");
                    }

                }

            }
        } else if (command.getName().equalsIgnoreCase("report")) {
            File dir = this.getDataFolder();
            File reportsFolder = new File(dir + File.separator + "PlayerReports" + File.separator);
            File playerDb = new File(reportsFolder, "PlayerReports.db");
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("playerreport.error")));
                return true;
            }

            if (args[0].equalsIgnoreCase("submit")) {
                if (args.length >= 2) {
                    if (!args[1].equalsIgnoreCase(sender.getName())) {
                        if (sender.hasPermission("metrocore.report")) {
                            String name = sender.getName();
                            String reportname = args[1].toString();
                            String report = "";
                            for (int i = 2; i < args.length; i++) {
                                report += args[i] + " ";
                            }

                            String report2 = report.replace("'", "''");
                            try {
                                String path = playerDb.getPath();
                                c = DriverManager.getConnection("jdbc:sqlite:" + path);
                                stmt = c.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                                rs = stmt.executeQuery("SELECT COUNT(*) FROM PLAYERREPORTS");
                                rs.next();
                                int rowCount = rs.getInt(1) + 1;
                                rs.close();


                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                                Date date = new Date(System.currentTimeMillis());
                                String currentDate = formatter.format(date);

                                String sql = "INSERT INTO PLAYERREPORTS (ID,PLAYER,REPORTED,REASON,DATE) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + reportname + "', '" + report2 + "', '" + currentDate + "' );";

                                ResultSet rsn = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                                rsn = stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                                rsn.next();
                                int reportCount = rsn.getInt("ROWCOUNT") + 1;
                                rsn.close();

                                ResultSet rsd = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                                rsd = stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNTREP FROM PLAYERREPORTS WHERE PLAYER = '" + reportname + "'");
                                rsd.next();
                                int reportCountReported = rsn.getInt("ROWCOUNTREP") + 1;
                                rsd.close();

                                stmt.executeUpdate(sql);
                                stmt.close();
                                c.close();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("playerreport.report_submitted")));
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if ((p.hasPermission("metrocore.admin")) || (p.hasPermission("metrocore.mod"))) {
                                        for (String s : this.getConfig().getStringList("playerreport.report_submitted_staff")) {
                                            String msg = s.replaceAll("%player%", name).replaceAll("%reported%", reportname).replaceAll("%reason%", report).replaceAll("%amount%", String.valueOf(reportCount));
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                                        }
                                    }
                                }

                                if (reportCountReported == 5) {
                                    Long guild = this.getConfig().getLong("discord.guild_id");
                                    Long channelid = this.getConfig().getLong("discord.watchlist_channel_id");
                                    java.awt.Color c = new java.awt.Color(0, 0, 255);
                                    EmbedBuilder eb = new EmbedBuilder();
                                    eb.setTitle("Metro auto-watchlist: " + reportname);
                                    eb.setDescription("**From:** The Watcher.\n" + "**About:** " + reportname + "\n" + "**Reason:** Player has received more than 5 reports.\n");
                                    eb.setColor(c);

                                    TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                                    ch.sendMessage(eb.build()).queue();
                                }

                                Long guild = this.getConfig().getLong("discord.guild_id");
                                Long channelid = this.getConfig().getLong("discord.playerreport_channel_id");
                                java.awt.Color c = new java.awt.Color(255, 0, 0);
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Player Report #" + rowCount);
                                eb.setDescription("**ID**: #" + rowCount + "\n" + "**From:** " + sender.getName() + "\n" + "**About**: " + reportname + "\n" + "**Reason:** " + report + "\n" + "**Date:** " + date);
                                eb.setColor(c);

                                TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                                ch.sendMessage(eb.build()).queue();

                                return true;
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("playerreport.database_error")));
                            }

                        }


                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("playerreport.cant_report_self")));
                        return true;
                    }

                }
            } else if (args[0].equalsIgnoreCase("history")) {
                if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                    if (args[1] != null) {
                        String name = args[1].toString();
                        try {
                            String path = playerDb.getPath();
                            c = DriverManager.getConnection("jdbc:sqlite:" + path);
                            stmt = c.createStatement();
                            ResultSet rsn = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rsn = stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                            rsn.next();
                            int reportCountSubmit = rsn.getInt("ROWCOUNT");
                            rsn.close();


                            sender.sendMessage(ChatColor.GOLD + name + "'s report history -");
                            sender.sendMessage(ChatColor.GREEN + "Submitted" + ChatColor.YELLOW + " [" + String.valueOf(reportCountSubmit) + "]" + ChatColor.GRAY + ":");
                            ResultSet rs = stmt.executeQuery("SELECT * FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                            while (rs.next()) {
                                int id = rs.getInt("ID");
                                String player = rs.getString("PLAYER");
                                String reported = rs.getString("REPORTED");
                                String reason = rs.getString("REASON");
                                String date = rs.getString("DATE");

                                String msg = this.getConfig().getString("playerreport.report_submitted_history_format").replaceAll("%date%", date).replaceAll("%player%", reported).replaceAll("%reason%", reason);
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                            }
                            rs.close();

                            ResultSet rsRec = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rsRec = stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNTREC FROM PLAYERREPORTS WHERE REPORTED = '" + name + "'");
                            rsRec.next();
                            int reportCountReceived = rsRec.getInt("ROWCOUNTREC");
                            rsRec.close();
                            ResultSet recieved = stmt.executeQuery("SELECT * FROM PLAYERREPORTS WHERE REPORTED = '" + sender.getName() + "'");
                            sender.sendMessage("\n" + ChatColor.RED + "Received" + ChatColor.YELLOW + " [" + String.valueOf(reportCountReceived) + "]" + ChatColor.GRAY + ":");
                            while (recieved.next()) {
                                int id = recieved.getInt("ID");
                                String player = recieved.getString("PLAYER");
                                String reported = recieved.getString("REPORTED");
                                String reason = recieved.getString("REASON");
                                String date = recieved.getString("DATE");
                                String msg = this.getConfig().getString("playerreport.report_received_history_format").replaceAll("%date%", date).replaceAll("%reporter%", player).replaceAll("%reason%", reason);
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                            }
                            rsRec.close();


                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify a player. &e[/report history <player>]");
                    }
                    return true;
                }
            }
        } else if (command.getName().equalsIgnoreCase("watchlist")) {
            if (sender.hasPermission("metrocore.mod")) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("add")) {
                        File dir = this.getDataFolder();
                        File reportsFolder = new File(dir + File.separator + "PlayerReports" + File.separator);
                        File playerDb = new File(reportsFolder, "PlayerReports.db");
                        String name = args[1].toString();
                        String report = "";
                        for (int i = 2; i < args.length; i++) {
                            report += args[i] + " ";
                        }

                        try {
                            String path = playerDb.getPath();
                            c = DriverManager.getConnection("jdbc:sqlite:" + path);
                            stmt = c.createStatement();
                            ResultSet rsRec = stmt.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rsRec = stmt.executeQuery("SELECT COUNT(*) AS ROWCOUNTREC FROM PLAYERREPORTS WHERE REPORTED = '" + name + "'");
                            rsRec.next();
                            int reportCountReceived = rsRec.getInt("ROWCOUNTREC");
                            rsRec.close();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("watchlist.added").replaceAll("%player%", name)));
                            Long guild = this.getConfig().getLong("discord.guild_id");
                            Long channelid = this.getConfig().getLong("discord.watchlist_channel_id");
                            java.awt.Color c = new java.awt.Color(0, 0, 255);
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Watch: " + name);
                            eb.setDescription("**From:** " + sender.getName() + "\n" + "**About:** " + name + "\n" + "**Reason:** " + report + "\n" + "**Previous reports:** " + String.valueOf(reportCountReceived));
                            eb.setColor(c);

                            TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                            ch.sendMessage(eb.build()).queue();
                            return true;


                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/watchlist add <username>]");
                    }

                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/watchlist add <username>]");
                }
            }

        } else if (command.getName().equalsIgnoreCase("suggestion")) {
            if (sender.hasPermission("metrocore.suggestion")) {
                if (args.length > 0) {
                    String report = "";
                    for (int i = 0; i < args.length; i++) {
                        report += args[i] + " ";
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("suggestion.added")));

                    Long guild = this.getConfig().getLong("discord.guild_id");
                    Long channelid = this.getConfig().getLong("discord.suggestions_channel_id");
                    java.awt.Color c = new java.awt.Color(255, 0, 255);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Suggestion from: " + sender.getName());
                    eb.setDescription("**Suggestion:** " + report);
                    eb.setColor(c);

                    TextChannel ch = jda.getGuildById(guild).getTextChannelById(channelid);
                    ch.sendMessage(eb.build()).queue();

                    return true;

                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/suggestion <text>]");
                }
            }
        }
        return true;
    }
}

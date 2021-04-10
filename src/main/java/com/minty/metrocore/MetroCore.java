package com.minty.metrocore;

import commands.*;
import filehandling.CreateAdminFile;
import filehandling.CreateModFile;
import filehandling.LogCommands;
import filehandling.PlayerJoinDatabase;
import listeners.*;
import methods.AdminMode;
import methods.ModMode;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.sql.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class MetroCore extends JavaPlugin {

    private static Connection c = null;
    private static Statement stmt = null;
    private final static ConsoleCommandSender sender = Bukkit.getConsoleSender();
    public static HashMap<Player, Boolean> Mod;
    public static HashMap<Player, Boolean> Admin;
    public static JDA jda;
    private PlayerStates playerStates;
    private PlayerJoinDatabase playerDatabase;
    private CreateAdminFile adminFile;
    private CreateModFile modFile;
    private LogCommands logCommands;
    private ModMode modMode;
    private AdminMode adminMode;
    // /yeet remake huehuehue
    // DiscordBot from server > bugreports || punishments  || player reports || ranks++ (Discord -> Minecraft <- Sync) || suggestions
    // /punish > GUI with selective options > Follows guidelines > Takes previous offenses into account too
    // PlayerCommandPreprocessEvent > Register commands
    // coinflip command
    // force commands to look for player
    //

    @Override
    public void onEnable() {

        try {
            jda = JDABuilder.createDefault("token").build();
            //jda.addEventListener(new DiscordListener());
            jda.awaitReady();
            this.getLogger().info("[MetroCore] Connected to Discord.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            this.getLogger().severe("[MetroCore] Cannot connect to Discord");
        }


        this.playerStates = new PlayerStates();
        this.playerDatabase = new PlayerJoinDatabase(this);
        this.adminFile = new CreateAdminFile(this);
        this.modFile = new CreateModFile(this);
        this.logCommands = new LogCommands(this, adminFile, modFile);
        this.modMode = new ModMode(this);
        this.adminMode = new AdminMode(this);

        getServer().getPluginManager().registerEvents(new LandClaimListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, playerDatabase, adminFile, modFile), this);
        getServer().getPluginManager().registerEvents(new CommandUseListener(this, logCommands), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, logCommands), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this, modMode, adminMode), this);

        this.getCommand("metrocore").setExecutor(new MetroCoreCommand(this));
        this.getCommand("mod").setExecutor(new ModCommand(this, modMode));
        this.getCommand("admin").setExecutor(new AdminCommand(this, adminMode));
        this.getCommand("notes").setExecutor(new NotesCommand(this));
        this.getCommand("rtp").setExecutor(new RandomTeleportCommand(this));
        this.getCommand("bugreport").setExecutor(new BugReportCommand(this));
        this.getCommand("report").setExecutor(new PlayerReportCommand(this));
        this.getCommand("watchlist").setExecutor(new WatchlistCommand(this));
        this.getCommand("suggestion").setExecutor(new SuggestionsCommand(this));



        getLogger().info("Enabled");
        Mod = new HashMap<>();
        Admin = new HashMap<>();
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
}

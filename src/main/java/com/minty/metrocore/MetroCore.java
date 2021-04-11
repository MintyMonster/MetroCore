package com.minty.metrocore;

import com.minty.metrocore.commands.*;
import com.minty.metrocore.filehandling.*;
import com.minty.metrocore.listeners.*;
import com.minty.metrocore.methods.AdminMode;
import com.minty.metrocore.methods.ModMode;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

import java.util.*;

public class MetroCore extends JavaPlugin {

    private final static ConsoleCommandSender sender = Bukkit.getConsoleSender();
    public static HashMap<Player, Boolean> Mod;
    public static HashMap<Player, Boolean> Admin;
    public static JDA jda;
    //private final PlayerStates playerStates;
    private PlayerJoinDatabase playerDatabase;
    private CreateAdminFile adminFile;
    private CreateModFile modFile;
    private LogCommands logCommands;
    private ModMode modMode;
    private AdminMode adminMode;
    private CreateDirectory createDirectory;
    private CreatePluginFiles createPluginFiles;
    private CreateDatabase createDatabase;

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

        this.playerDatabase = new PlayerJoinDatabase(this);
        this.adminFile = new CreateAdminFile(this);
        this.modFile = new CreateModFile(this);
        this.logCommands = new LogCommands(this, adminFile, modFile);
        this.modMode = new ModMode(this);
        this.adminMode = new AdminMode(this);
        this.createDirectory = new CreateDirectory(this);
        this.createPluginFiles = new CreatePluginFiles(this, createDirectory);
        this.createDatabase = new CreateDatabase(this, createDirectory);

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

        createDirectory.createDir("Logs");
        createDirectory.createDir("AidanNotes");
        createDirectory.createDir("BugReports");
        createDirectory.createDir("PlayerReports");
        createDirectory.createDir("PlayerData");

        createPluginFiles.createFile("PlayerData.db", "PlayerData");
        createPluginFiles.createFile("AidanNotes.db", "AidanNotes");
        createPluginFiles.createFile("BugReports.db", "BugReports");
        createPluginFiles.createFile("PlayerReports.db", "PlayerReports");

        String sql = "CREATE TABLE IF NOT EXISTS PLAYERDATA (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, UUID TEXT NOT NULL, IP TEXT NOT NULL, DATE TEXT NOT NULL)";
        createDatabase.createDb("PlayerData", "PlayerData.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS BUGREPORTS (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, REPORT TEXT NOT NULL, STATUS TEXT NOT NULL)";
        createDatabase.createDb("BugReports", "BugReports.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS PLAYERREPORTS (ID INTEGER PRIMARY KEY NOT NULL, PLAYER TEXT NOT NULL, REPORTED TEXT NOT NULL, REASON TEXT NOT NULL, DATE TEXT NOT NULL)";
        createDatabase.createDb("PlayerReports", "PlayerReports.db", sql);
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }
}
package com.minty.metrocore;

import com.minty.metrocore.commands.*;
import com.minty.metrocore.database.BugReportsDatabase;
import com.minty.metrocore.database.NotesDatabase;
import com.minty.metrocore.database.PlayerReportDatabase;
import com.minty.metrocore.discord.SendMessageToChannel;
import com.minty.metrocore.filehandling.*;
import com.minty.metrocore.listeners.*;
import com.minty.metrocore.managers.CommandManager;
import com.minty.metrocore.methods.AdminMode;
import com.minty.metrocore.methods.ModMode;
import com.minty.metrocore.methods.PlayerStates;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

import java.util.*;

public class MetroCore extends JavaPlugin {

    private final static ConsoleCommandSender sender = Bukkit.getConsoleSender();
    public static JDA jda;
    private PlayerStates playerStates;
    private PlayerJoinDatabase playerDatabase;
    private LogCommands logCommands;
    private ModMode modMode;
    private AdminMode adminMode;
    private CreateDirectory createDirectory;
    private CreatePluginFiles createPluginFiles;
    private CreateDatabase createDatabase;
    private SendMessageToChannel sendMessageToChannel;
    private CommandManager commandManager;
    private BugReportsDatabase bugReportsDatabase;
    private PlayerReportDatabase playerReportDatabase;
    private NotesDatabase notesDatabase;

    // To do list
    // tab completion for all commands
    // learn how to use turnary
    // learn how to use lambda
    // Create message manager

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        try {
            jda = JDABuilder.createDefault("token").build();
            //jda.addEventListener(new DiscordListener());
            jda.awaitReady();
            this.getLogger().info("[MetroCore] Connected to Discord.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            this.getLogger().severe("[MetroCore] Cannot connect to Discord");
        }

        // Don't need a hard reference necessarily????
        this.playerStates = new PlayerStates();
        this.createDirectory = new CreateDirectory(this);
        this.createPluginFiles = new CreatePluginFiles(this, createDirectory);
        this.createDatabase = new CreateDatabase(this, createDirectory);
        this.playerDatabase = new PlayerJoinDatabase(this);
        this.logCommands = new LogCommands(this, createPluginFiles);
        this.modMode = new ModMode(this, playerStates);
        this.adminMode = new AdminMode(this, playerStates);
        this.bugReportsDatabase = new BugReportsDatabase(this);
        this.playerReportDatabase = new PlayerReportDatabase(this);
        this.notesDatabase = new NotesDatabase(this);
        this.commandManager = new CommandManager(this,
                sendMessageToChannel, bugReportsDatabase,
                adminMode, modMode, playerStates,
                playerReportDatabase, notesDatabase);

        this.sendMessageToChannel = new SendMessageToChannel(this);

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new LandClaimListener(this), this);
        pluginManager.registerEvents(new PlayerJoinListener(playerDatabase, playerStates, createPluginFiles), this);
        pluginManager.registerEvents(new CommandUseListener(this, logCommands, playerStates), this);
        pluginManager.registerEvents(new ChatListener(this, logCommands, playerStates), this);
        pluginManager.registerEvents(new PlayerLeaveListener(modMode, adminMode), this);



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
        sql = "CREATE TABLE IF NOT EXISTS AIDANNOTES (ID INTEGER PRIMARY KEY NOT NULL, NOTE TEXT NOT NULL, STATUS TEXT NOT NULL)";
        createDatabase.createDb("AidanNotes", "AidanNotes.db", sql);

        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }

    public void sendWithPrefix(Player player, String message){
        String prefix = this.getConfig().getString("prefix");

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
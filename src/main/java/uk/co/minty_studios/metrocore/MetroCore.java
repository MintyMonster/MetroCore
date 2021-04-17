package uk.co.minty_studios.metrocore;

import litebans.api.Entry;
import litebans.api.Events;
import uk.co.minty_studios.metrocore.database.BugReportsDatabase;
import uk.co.minty_studios.metrocore.database.DiscordCodeDatabase;
import uk.co.minty_studios.metrocore.database.NotesDatabase;
import uk.co.minty_studios.metrocore.database.PlayerReportDatabase;
import uk.co.minty_studios.metrocore.discord.DiscordLinking;
import uk.co.minty_studios.metrocore.discord.MessageReceivedListener;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
import uk.co.minty_studios.metrocore.filehandling.*;
import uk.co.minty_studios.metrocore.listeners.*;
import uk.co.minty_studios.metrocore.managers.CommandManager;
import uk.co.minty_studios.metrocore.methods.AdminMode;
import uk.co.minty_studios.metrocore.methods.GenericUseMethods;
import uk.co.minty_studios.metrocore.methods.ModMode;
import uk.co.minty_studios.metrocore.methods.PlayerStates;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.UUID;

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
    private DiscordLinking discordLinking;
    private DiscordCodeDatabase discordCodeDatabase;
    private GenericUseMethods genericUseMethods;

    // To do list
    // tab completion for all commands
    // learn how to use turnary
    // learn how to use lambda
    // Create message manager
    // Ask Brianna about CommandManager adding just a master command without child

    // Error messages for commands
    // Metro discord srv thing (PAPI)
    // punishment gui > with discord implementation || Use HashMap > Store Staff Name + Person getting punished name > Check against the 2 // check previous punishments
    // Rank linking > DM based? (Hashset/Map) > link > Add "linked" role > relink > remove role then add new role > unlink > remove role
    // help command
    // Discord equivalents of in-game messages (!tps, !ban, !mute etc)
    // emotes :angry: > message + head??
    // Add pages to notes, bugreports, notes
    // Mod mode gives 2 messages (to admins/mods)
    // RTP tps to top of nether


    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        // Don't need a hard reference necessarily????
        this.playerStates = new PlayerStates();
        this.genericUseMethods = new GenericUseMethods(this);
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
        this.sendMessageToChannel = new SendMessageToChannel(this);
        this.discordCodeDatabase = new DiscordCodeDatabase(this);
        this.discordLinking = new DiscordLinking();
        this.commandManager = new CommandManager(this,
                sendMessageToChannel, bugReportsDatabase,
                adminMode, modMode, playerStates,
                playerReportDatabase, notesDatabase, discordLinking,
                discordCodeDatabase, genericUseMethods);



        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new LandClaimListener(this), this);
        pluginManager.registerEvents(new PlayerJoinListener(playerDatabase, playerStates, createPluginFiles), this);
        pluginManager.registerEvents(new CommandUseListener(this, logCommands, playerStates), this);
        pluginManager.registerEvents(new ChatListener(this, logCommands, playerStates), this);
        pluginManager.registerEvents(new PlayerLeaveListener(modMode, adminMode, discordLinking), this);



        createDirectory.createDir("Logs");
        createDirectory.createDir("AidanNotes");
        createDirectory.createDir("BugReports");
        createDirectory.createDir("PlayerReports");
        createDirectory.createDir("PlayerData");
        createDirectory.createDir("DiscordLink");

        createPluginFiles.createFile("PlayerData.db", "PlayerData");
        createPluginFiles.createFile("AidanNotes.db", "AidanNotes");
        createPluginFiles.createFile("BugReports.db", "BugReports");
        createPluginFiles.createFile("PlayerReports.db", "PlayerReports");
        createPluginFiles.createFile("DiscordLink.db", "DiscordLink");

        String sql = "CREATE TABLE IF NOT EXISTS PLAYERDATA (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, UUID TEXT NOT NULL, IP TEXT NOT NULL, DATE TEXT NOT NULL)";
        createDatabase.createDb("PlayerData", "PlayerData.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS BUGREPORTS (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, REPORT TEXT NOT NULL, STATUS TEXT NOT NULL)";
        createDatabase.createDb("BugReports", "BugReports.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS PLAYERREPORTS (ID INTEGER PRIMARY KEY NOT NULL, PLAYER TEXT NOT NULL, REPORTED TEXT NOT NULL, REASON TEXT NOT NULL, DATE TEXT NOT NULL)";
        createDatabase.createDb("PlayerReports", "PlayerReports.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS AIDANNOTES (ID INTEGER PRIMARY KEY NOT NULL, NOTE TEXT NOT NULL, STATUS TEXT NOT NULL)";
        createDatabase.createDb("AidanNotes", "AidanNotes.db", sql);
        sql = "CREATE TABLE IF NOT EXISTS DISCORDLINK (UUID TEXT PRIMARY KEY NOT NULL, ID INTEGER NOT NULL, CODE INTEGER NOT NULL)";
        createDatabase.createDb("DiscordLink", "DiscordLink.db", sql);

        try {
            jda = JDABuilder.createDefault("token").build();
            jda.addEventListener(new MessageReceivedListener(this, discordLinking, discordCodeDatabase, sendMessageToChannel, genericUseMethods));
            jda.awaitReady();
            this.getLogger().info("[MetroCore] Connected to Discord.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            this.getLogger().severe("[MetroCore] Cannot connect to Discord");
        }

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
package com.minty.metrocore.managers;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.commands.MasterCommand;
import com.minty.metrocore.commands.admincommands.AdminOffCommand;
import com.minty.metrocore.commands.admincommands.AdminOnCommand;
import com.minty.metrocore.commands.core.ReloadCommand;
import com.minty.metrocore.commands.bugreport.FixCommand;
import com.minty.metrocore.commands.bugreport.ListCommand;
import com.minty.metrocore.commands.bugreport.SubmitCommand;
import com.minty.metrocore.commands.modcommands.ModOffCommand;
import com.minty.metrocore.commands.modcommands.ModOnCommand;
import com.minty.metrocore.commands.notescommands.NotesAddCommand;
import com.minty.metrocore.commands.notescommands.NotesListCommand;
import com.minty.metrocore.commands.notescommands.NotesRemoveCommand;
import com.minty.metrocore.commands.othercommands.RTPCommand;
import com.minty.metrocore.commands.playerreport.ReportHistoryCommand;
import com.minty.metrocore.commands.playerreport.ReportSubmitCommand;
import com.minty.metrocore.commands.suggestioncommands.SuggestionsAddCommand;
import com.minty.metrocore.commands.watchlistcommands.WatchlistAddCommand;
import com.minty.metrocore.database.BugReportsDatabase;
import com.minty.metrocore.database.NotesDatabase;
import com.minty.metrocore.database.PlayerReportDatabase;
import com.minty.metrocore.discord.SendMessageToChannel;
import com.minty.metrocore.methods.AdminMode;
import com.minty.metrocore.methods.ModMode;
import com.minty.metrocore.methods.PlayerStates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor {

    private final Map<String, MasterCommand> commands = new HashMap<>();
    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final BugReportsDatabase bugReportsDatabase;
    private final PlayerReportDatabase playerReportDatabase;
    private final AdminMode adminMode;
    private final ModMode modMode;
    private final PlayerStates playerStates;
    private final NotesDatabase notesDatabase;

    public CommandManager(MetroCore plugin,
                          SendMessageToChannel sendMessageToChannel,
                          BugReportsDatabase bugReportsDatabase,
                          AdminMode adminMode,
                          ModMode modMode,
                          PlayerStates playerStates,
                          PlayerReportDatabase playerReportDatabase,
                          NotesDatabase notesDatabase) {

        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
        this.bugReportsDatabase = bugReportsDatabase;
        this.modMode = modMode;
        this.adminMode = adminMode;
        this.playerStates = playerStates;
        this.playerReportDatabase = playerReportDatabase;
        this.notesDatabase = notesDatabase;

        plugin.getCommand("metrocore").setExecutor(this);
        plugin.getCommand("bugreport").setExecutor(this);
        plugin.getCommand("admin").setExecutor(this);
        plugin.getCommand("mod").setExecutor(this);
        plugin.getCommand("report").setExecutor(this);
        plugin.getCommand("rtp").setExecutor(new RTPCommand(plugin));
        plugin.getCommand("notes").setExecutor(this);
        plugin.getCommand("watchlist").setExecutor(this);
        plugin.getCommand("suggestions").setExecutor(this);



        addCommands("metrocore",
                new ReloadCommand("reload", plugin)
        );

        addCommands("bugreport",
                new FixCommand("fix", plugin, sendMessageToChannel, bugReportsDatabase),
                new ListCommand("list", plugin, bugReportsDatabase),
                new SubmitCommand("submit", plugin, sendMessageToChannel, bugReportsDatabase)
        );

        addCommands("admin",
                new AdminOnCommand("on", adminMode),
                new AdminOffCommand("off", adminMode));

        addCommands("mod",
                new ModOnCommand("on", modMode),
                new ModOffCommand("off", modMode));

        addCommands("report",
                new ReportHistoryCommand("history", plugin, playerReportDatabase),
                new ReportSubmitCommand("submit", plugin, sendMessageToChannel, playerReportDatabase));

        addCommands("notes",
                new NotesAddCommand("add", plugin, sendMessageToChannel, notesDatabase),
                new NotesListCommand("list", plugin, notesDatabase),
                new NotesRemoveCommand("remove", plugin, notesDatabase));

        addCommands("watchlist",
                new WatchlistAddCommand("add", plugin, sendMessageToChannel, playerReportDatabase));

        addCommands("suggestions",
                new SuggestionsAddCommand("", plugin, sendMessageToChannel));

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length > 0) {
            MasterCommand master = commands.get(command.getName().toLowerCase());
            if (master != null) {
                for (ChildCommand child : master.getChildCommands().values())
                    if (args[0].equalsIgnoreCase(child.getName()))
                        child.perform(player, args);

                return true;
            }
        }

        return true;
    }

    public void addCommand(String command, ChildCommand child) {
        commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand()).addCommand(child);
    }

    public void addCommands(String command, ChildCommand... children) {
        MasterCommand master = commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand());
        for (ChildCommand child : children)
            master.addCommand(child);
    }

}

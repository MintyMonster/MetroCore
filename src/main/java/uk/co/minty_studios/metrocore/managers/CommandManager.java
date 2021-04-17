package uk.co.minty_studios.metrocore.managers;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.commands.MasterCommand;
import uk.co.minty_studios.metrocore.commands.admincommands.AdminOffCommand;
import uk.co.minty_studios.metrocore.commands.admincommands.AdminOnCommand;
import uk.co.minty_studios.metrocore.commands.corecommands.ReloadCommand;
import uk.co.minty_studios.metrocore.commands.bugreport.FixCommand;
import uk.co.minty_studios.metrocore.commands.bugreport.ListCommand;
import uk.co.minty_studios.metrocore.commands.bugreport.SubmitCommand;
import uk.co.minty_studios.metrocore.commands.discordcommands.LinkDiscordCommand;
import uk.co.minty_studios.metrocore.commands.discordcommands.UnlinkCommand;
import uk.co.minty_studios.metrocore.commands.modcommands.ModOffCommand;
import uk.co.minty_studios.metrocore.commands.modcommands.ModOnCommand;
import uk.co.minty_studios.metrocore.commands.notescommands.NotesAddCommand;
import uk.co.minty_studios.metrocore.commands.notescommands.NotesListCommand;
import uk.co.minty_studios.metrocore.commands.notescommands.NotesRemoveCommand;
import uk.co.minty_studios.metrocore.commands.othercommands.RTPCommand;
import uk.co.minty_studios.metrocore.commands.playerreport.ReportHistoryCommand;
import uk.co.minty_studios.metrocore.commands.playerreport.ReportSubmitCommand;
import uk.co.minty_studios.metrocore.commands.suggestioncommands.SuggestionsAddCommand;
import uk.co.minty_studios.metrocore.commands.watchlistcommands.WatchlistAddCommand;
import uk.co.minty_studios.metrocore.database.BugReportsDatabase;
import uk.co.minty_studios.metrocore.database.DiscordCodeDatabase;
import uk.co.minty_studios.metrocore.database.NotesDatabase;
import uk.co.minty_studios.metrocore.database.PlayerReportDatabase;
import uk.co.minty_studios.metrocore.discord.DiscordLinking;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
import uk.co.minty_studios.metrocore.methods.AdminMode;
import uk.co.minty_studios.metrocore.methods.GenericUseMethods;
import uk.co.minty_studios.metrocore.methods.ModMode;
import uk.co.minty_studios.metrocore.methods.PlayerStates;
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
    private final DiscordLinking discordLinking;
    private final DiscordCodeDatabase discordCodeDatabase;
    private final GenericUseMethods genericUseMethods;

    public CommandManager(MetroCore plugin,
                          SendMessageToChannel sendMessageToChannel,
                          BugReportsDatabase bugReportsDatabase,
                          AdminMode adminMode,
                          ModMode modMode,
                          PlayerStates playerStates,
                          PlayerReportDatabase playerReportDatabase,
                          NotesDatabase notesDatabase,
                          DiscordLinking discordLinking,
                          DiscordCodeDatabase discordCodeDatabase, GenericUseMethods genericUseMethods) {

        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
        this.bugReportsDatabase = bugReportsDatabase;
        this.modMode = modMode;
        this.adminMode = adminMode;
        this.playerStates = playerStates;
        this.playerReportDatabase = playerReportDatabase;
        this.notesDatabase = notesDatabase;
        this.discordLinking = discordLinking;
        this.discordCodeDatabase = discordCodeDatabase;
        this.genericUseMethods = genericUseMethods;

        plugin.getCommand("metrocore").setExecutor(this);
        plugin.getCommand("bugreport").setExecutor(this);
        plugin.getCommand("admin").setExecutor(this);
        plugin.getCommand("mod").setExecutor(this);
        plugin.getCommand("report").setExecutor(this);
        plugin.getCommand("rtp").setExecutor(new RTPCommand(plugin));
        plugin.getCommand("notes").setExecutor(this);
        plugin.getCommand("watchlist").setExecutor(this);
        plugin.getCommand("suggestions").setExecutor(this);
        plugin.getCommand("discord").setExecutor(this);



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

        addCommands("playerreport",
                new ReportHistoryCommand("history", plugin, playerReportDatabase),
                new ReportSubmitCommand("submit", plugin, sendMessageToChannel, playerReportDatabase));

        addCommands("notes",
                new NotesAddCommand("add", plugin, sendMessageToChannel, notesDatabase),
                new NotesListCommand("list", plugin, notesDatabase),
                new NotesRemoveCommand("remove", plugin, notesDatabase));

        addCommands("watchlist",
                new WatchlistAddCommand("add", plugin, sendMessageToChannel, playerReportDatabase));

        addCommands("suggestions",
                new SuggestionsAddCommand("submit", plugin, sendMessageToChannel));

        addCommands("discord",
                new LinkDiscordCommand("link", discordLinking, plugin, discordCodeDatabase),
                new UnlinkCommand("unlink", plugin, discordLinking, discordCodeDatabase, genericUseMethods));

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
                        if(player.hasPermission(child.getPermission()))
                            child.perform(player, args);
                        else
                            plugin.sendWithPrefix(player, "&cPermission denied");

                return true;
            }
        }else{
            MasterCommand master = commands.get(command.getName().toLowerCase());
            if(master != null){
                master.getChildCommands().values().forEach(c -> {
                    if(player.hasPermission(c.getPermission()))
                        plugin.sendWithPrefix(player, "&cUsage: " + c.getSyntax());
                });
            }

        }

        return true;
    }

    public void addCommand(String command, ChildCommand child) {
        commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand()).addCommand(child);
    }

    public void addCommands(String command, ChildCommand... children) {
        MasterCommand master = commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand()); // research this and the one above
        for (ChildCommand child : children)
            master.addCommand(child);
    }

}

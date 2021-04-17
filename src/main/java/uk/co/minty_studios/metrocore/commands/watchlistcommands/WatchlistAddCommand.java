package uk.co.minty_studios.metrocore.commands.watchlistcommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.PlayerReportDatabase;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
import org.bukkit.entity.Player;

import java.awt.*;

public class WatchlistAddCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final PlayerReportDatabase playerReportDatabase;

    public WatchlistAddCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel, PlayerReportDatabase playerReportDatabase) {
        super(command);
        this.sendMessageToChannel = sendMessageToChannel;
        this.plugin = plugin;
        this.playerReportDatabase = playerReportDatabase;

    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Add player to watchlist!";
    }

    @Override
    public String getSyntax() {
        return "&e/watchlist add <player> <reason>";
    }

    @Override
    public void perform(Player player, String[] args) {

        if(!(args.length > 2)){
            plugin.sendWithPrefix(player, "&cUsage: &e/watchlist add <player> <reason>");
            return;
        }

        Long guild = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.watchlist_channel_id");
        int reportCount = playerReportDatabase.getTotalReportsAboutPlayer(args[1]);

        String reason = "";
        for(int i = 2; i < args.length; i++)
            reason += args[i] + " ";

        sendMessageToChannel.sendEmbed(guild, channelid, new Color(0, 0, 255), "Watch: " + args[1],
                "**From:** " + player.getName() + "\n" + "**About:** " + args[1] + "\n" + "**Reason:** " + reason + "\n" + "**Previous reports:** " + reportCount);

        plugin.sendWithPrefix(player, plugin.getConfig().getString("watchlist.added").replace("%player%", args[1]));

    }
}

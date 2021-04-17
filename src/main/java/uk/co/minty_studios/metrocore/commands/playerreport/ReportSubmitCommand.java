package uk.co.minty_studios.metrocore.commands.playerreport;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.PlayerReportDatabase;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;

public class ReportSubmitCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final PlayerReportDatabase playerReportDatabase;

    public ReportSubmitCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel, PlayerReportDatabase playerReportDatabase) {
        super(command);
        this.playerReportDatabase = playerReportDatabase;
        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
    }

    @Override
    public String getPermission(){ return "metrocore.core"; }

    @Override
    public String getDescription() {
        return "Submit a player report!";
    }

    @Override
    public String getSyntax() {
        return "&e/report submit <player> <reason>";
    }

    @Override
    public void perform(Player player, String[] args) {

        Long guildid = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.playerreport_channel_id");
        Long watchlistid = plugin.getConfig().getLong("discord.watchlist_channel_id");

        if(!(args.length >= 3)){
            plugin.sendWithPrefix(player, "&cUsage: &e/report submit <player> <reason>");
            return;
        }

        if(args[1].equals(player.getName())){
            plugin.sendWithPrefix(player, "&cYou cannot report yourself.");
            return;
        }

        String reportName = args[1];
        String name = player.getName();
        int totalCount = playerReportDatabase.getTotalCount();
        int reportCount = playerReportDatabase.getTotalReportsFromPlayer(name);
        int totalReportAbout = playerReportDatabase.getTotalReportsAboutPlayer(reportName);

        String report = "";
        for(int i = 2; i < args.length; i++)
            report += args[i].replace("'", "''") + " ";

        playerReportDatabase.submitPlayerReport(name, reportName, report);

        plugin.sendWithPrefix(player, plugin.getConfig().getString("playerreport.report_submitted"));

        for(Player p : Bukkit.getOnlinePlayers())
            if(p.hasPermission("metrocore.admin"))
                for(String message : plugin.getConfig().getStringList("playerreport.report_submitted_staff"))
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            message.replaceAll("%player%", name)
                                    .replaceAll("%reported%", reportName)
                                    .replaceAll("%reason%", report)
                                    .replaceAll("%amount%", String.valueOf(reportCount))));

        if(totalReportAbout == 5)
            sendMessageToChannel.sendEmbed(guildid, watchlistid, new Color(0, 0, 255), "Metro auto-watchlist: " + reportName,
                    "**From:** The Watcher.\n" + "**About:** " + reportName + "\n" + "**Reason:** Player has received more than 5 reports.\n");

        sendMessageToChannel.sendEmbed(guildid, channelid, new Color(255, 0, 0), "Player report: #" + totalCount,
                "**ID**: #" + totalCount + "\n" + "**From:** " + player.getName() + "\n" + "**About**: " + reportName + "\n" + "**Reason:** " + report);
    }
}

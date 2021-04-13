package com.minty.metrocore.commands.playerreport;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.PlayerReportDatabase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReportHistoryCommand extends ChildCommand {

    private final MetroCore plugin;
    private final PlayerReportDatabase playerReportDatabase;

    public ReportHistoryCommand(String command, MetroCore plugin, PlayerReportDatabase playerReportDatabase) {
        super(command);
        this.plugin = plugin;
        this.playerReportDatabase = playerReportDatabase;
    }

    @Override
    public String getDescription() {
        return "Player's report history";
    }

    @Override
    public String getSyntax() {
        return "/report history <player>";
    }

    @Override
    public void perform(Player player, String[] args) {

        if(args[1] == null){
            plugin.sendWithPrefix(player, "&cUsage: &e/report history <player>");
            return;
        }

        if(player.hasPermission("metrocore.admin")){

            String name = args[1];
            int reportCountSubmit = playerReportDatabase.getTotalReportsFromPlayer(name);
            int reportCountAbout = playerReportDatabase.getTotalReportsAboutPlayer(name);

            player.sendMessage(ChatColor.GOLD + name + "'s report history -");
            player.sendMessage(ChatColor.GREEN + "Submitted" + ChatColor.YELLOW + " [" + reportCountSubmit + "]" + ChatColor.GRAY + ":");

            playerReportDatabase.getReportHistory(player, name);

            player.sendMessage("\n" + ChatColor.RED + "Received" + ChatColor.YELLOW + " [" + reportCountAbout + "]" + ChatColor.GRAY + ":");

            playerReportDatabase.getReportsAbout(player, name);

        }
    }
}

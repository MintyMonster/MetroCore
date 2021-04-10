package commands;

import com.minty.metrocore.MetroCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerReportCommand implements CommandExecutor {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public PlayerReportCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        File dir = plugin.getDataFolder();
        File reportsFolder = new File(dir + File.separator + "PlayerReports" + File.separator);
        File playerDb = new File(reportsFolder, "PlayerReports.db");
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playerreport.error")));
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
                            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                            statement = connection.createStatement();
                            ResultSet rs = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rs = statement.executeQuery("SELECT COUNT(*) FROM PLAYERREPORTS");
                            rs.next();
                            int rowCount = rs.getInt(1) + 1;
                            rs.close();


                            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                            Date date = new Date(System.currentTimeMillis());
                            String currentDate = formatter.format(date);

                            String sql = "INSERT INTO PLAYERREPORTS (ID,PLAYER,REPORTED,REASON,DATE) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + reportname + "', '" + report2 + "', '" + currentDate + "' );";

                            ResultSet rsn = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rsn = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                            rsn.next();
                            int reportCount = rsn.getInt("ROWCOUNT") + 1;
                            rsn.close();

                            ResultSet rsd = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                            rsd = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNTREP FROM PLAYERREPORTS WHERE PLAYER = '" + reportname + "'");
                            rsd.next();
                            int reportCountReported = rsn.getInt("ROWCOUNTREP") + 1;
                            rsd.close();

                            statement.executeUpdate(sql);
                            statement.close();
                            connection.close();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playerreport.report_submitted")));
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if ((p.hasPermission("metrocore.admin")) || (p.hasPermission("metrocore.mod"))) {
                                    for (String s : plugin.getConfig().getStringList("playerreport.report_submitted_staff")) {
                                        String msg = s.replaceAll("%player%", name).replaceAll("%reported%", reportname).replaceAll("%reason%", report).replaceAll("%amount%", String.valueOf(reportCount));
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                                    }
                                }
                            }

                            if (reportCountReported == 5) {
                                Long guild = plugin.getConfig().getLong("discord.guild_id");
                                Long channelid = plugin.getConfig().getLong("discord.watchlist_channel_id");
                                java.awt.Color c = new java.awt.Color(0, 0, 255);
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Metro auto-watchlist: " + reportname);
                                eb.setDescription("**From:** The Watcher.\n" + "**About:** " + reportname + "\n" + "**Reason:** Player has received more than 5 reports.\n");
                                eb.setColor(c);

                                TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                                ch.sendMessage(eb.build()).queue();
                            }

                            Long guild = plugin.getConfig().getLong("discord.guild_id");
                            Long channelid = plugin.getConfig().getLong("discord.playerreport_channel_id");
                            java.awt.Color c = new java.awt.Color(255, 0, 0);
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Player Report #" + rowCount);
                            eb.setDescription("**ID**: #" + rowCount + "\n" + "**From:** " + sender.getName() + "\n" + "**About**: " + reportname + "\n" + "**Reason:** " + report + "\n" + "**Date:** " + date);
                            eb.setColor(c);

                            TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                            ch.sendMessage(eb.build()).queue();

                            return true;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playerreport.database_error")));
                        }

                    }


                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playerreport.cant_report_self")));
                    return true;
                }

            }
        } else if (args[0].equalsIgnoreCase("history")) {
            if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                if (args[1] != null) {
                    String name = args[1].toString();
                    try {
                        String path = playerDb.getPath();
                        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                        statement = connection.createStatement();
                        ResultSet rsn = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                        rsn = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNT FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                        rsn.next();
                        int reportCountSubmit = rsn.getInt("ROWCOUNT");
                        rsn.close();


                        sender.sendMessage(ChatColor.GOLD + name + "'s report history -");
                        sender.sendMessage(ChatColor.GREEN + "Submitted" + ChatColor.YELLOW + " [" + String.valueOf(reportCountSubmit) + "]" + ChatColor.GRAY + ":");
                        ResultSet rs = statement.executeQuery("SELECT * FROM PLAYERREPORTS WHERE PLAYER = '" + name + "'");
                        while (rs.next()) {
                            int id = rs.getInt("ID");
                            String player = rs.getString("PLAYER");
                            String reported = rs.getString("REPORTED");
                            String reason = rs.getString("REASON");
                            String date = rs.getString("DATE");

                            String msg = plugin.getConfig().getString("playerreport.report_submitted_history_format").replaceAll("%date%", date).replaceAll("%player%", reported).replaceAll("%reason%", reason);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        }
                        rs.close();

                        ResultSet rsRec = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                        rsRec = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNTREC FROM PLAYERREPORTS WHERE REPORTED = '" + name + "'");
                        rsRec.next();
                        int reportCountReceived = rsRec.getInt("ROWCOUNTREC");
                        rsRec.close();
                        ResultSet recieved = statement.executeQuery("SELECT * FROM PLAYERREPORTS WHERE REPORTED = '" + sender.getName() + "'");
                        sender.sendMessage("\n" + ChatColor.RED + "Received" + ChatColor.YELLOW + " [" + String.valueOf(reportCountReceived) + "]" + ChatColor.GRAY + ":");
                        while (recieved.next()) {
                            int id = recieved.getInt("ID");
                            String player = recieved.getString("PLAYER");
                            String reported = recieved.getString("REPORTED");
                            String reason = recieved.getString("REASON");
                            String date = recieved.getString("DATE");
                            String msg = plugin.getConfig().getString("playerreport.report_received_history_format").replaceAll("%date%", date).replaceAll("%reporter%", player).replaceAll("%reason%", reason);
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
        return true;
    }
}

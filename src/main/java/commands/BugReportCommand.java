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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class BugReportCommand implements CommandExecutor {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public BugReportCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        File dir = plugin.getDataFolder();
        File bugFolder = new File(dir + File.separator + "BugReports" + File.separator);
        File bugReport = new File(bugFolder, "BugReports.db");
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bugreport.error")));
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                try {
                    String path = bugReport.getPath();
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                    statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT ID, NAME, REPORT, STATUS FROM BUGREPORTS");
                    while (rs.next()) {
                        int id = rs.getInt("ID");
                        String name = rs.getString("NAME");
                        String report = rs.getString("REPORT");
                        String result = rs.getString("STATUS");
                        if (result.equalsIgnoreCase("fixed")) {
                            String message = plugin.getConfig().getString("bugreport.list_formatting").replaceAll("%id%", String.valueOf(id)).replaceAll("%name%", name).replaceAll("%report%", report);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.GREEN + " [Fixed]");
                        } else {
                            String message = plugin.getConfig().getString("bugreport.list_formatting").replaceAll("%id%", String.valueOf(id)).replaceAll("%name%", name).replaceAll("%report%", report);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message) + ChatColor.RED + " [" + result + "]");
                        }


                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Permission denied");
            }


        } else if (args[0].equalsIgnoreCase("submit")) {
            if (args.length >= 2) {
                if (sender.hasPermission("metrocore.bugreport")) {
                    String name = sender.getName();
                    String report = "";
                    for (int i = 1; i < args.length; i++) {
                        report += args[i].replace("'", "''") + " ";
                    }

                    try {
                        String path = bugReport.getPath();
                        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                        statement = connection.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM BUGREPORTS");
                        rs.next();
                        int rowCount = rs.getInt(1) + 1;
                        rs.close();
                        statement.executeUpdate("INSERT INTO BUGREPORTS (ID,NAME,REPORT,STATUS) VALUES (" + String.valueOf(rowCount) + ", '" + name + "', '" + report + "', 'In progress' );");
                        statement.close();
                        connection.close();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bugreport.report_submitted")));
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if ((p.hasPermission("metrocore.admin")) || (p.hasPermission("metrocore.mod"))) {
                                String msg = plugin.getConfig().getString("bugreport.report_submitted_staff").replaceAll("%player%", sender.getName());
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                            }
                        }

                        Long guild = plugin.getConfig().getLong("discord.guild_id");
                        Long channelid = plugin.getConfig().getLong("discord.bugreport_channel_id");
                        java.awt.Color c = new java.awt.Color(255, 0, 0);
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Bug Report: #" + rowCount);
                        eb.setDescription("**ID:** #" + rowCount + "\n" + "**From:** " + sender.getName() + "\n" + "**Description:** " + report);
                        eb.setColor(c);

                        TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                        ch.sendMessage(eb.build()).queue();

                        return true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bugreport.database_error")));
                    }
                }
            }
        } else if ((args[0].equalsIgnoreCase("fixed")) || args[0].equalsIgnoreCase("fix")) {
            if ((sender.hasPermission("metrocore.admin")) || (sender.hasPermission("metrocore.mod"))) {
                if (args[1] != null) {
                    try {
                        String path = bugReport.getPath();
                        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                        statement = connection.createStatement();

                        String sql = "UPDATE BUGREPORTS SET STATUS = 'Fixed' WHERE ID = " + args[1].toString();
                        statement.executeUpdate(sql);
                        statement.close();
                        connection.close();
                        String message = plugin.getConfig().getString("bugreport.bugreport_fixed").replaceAll("%id%", args[1].toString());
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                        Long guild = plugin.getConfig().getLong("discord.guild_id");
                        Long channelid = plugin.getConfig().getLong("discord.bugreport_channel_id");
                        java.awt.Color c = new java.awt.Color(0, 200, 255);
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Bug Report: #" + args[1]);
                        eb.setDescription("Marked as **[Fixed]**");
                        eb.setColor(c);

                        TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                        ch.sendMessage(eb.build()).queue();

                        return true;

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Please provide an id to fix problem " + ChatColor.YELLOW + "[/bugreport fixed [id] ]" + ChatColor.RED + "\nUse " + ChatColor.YELLOW + "[/bugreport list]" + ChatColor.RED + "to see ids");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Permission denied");
            }

        }

        return true;
    }
}

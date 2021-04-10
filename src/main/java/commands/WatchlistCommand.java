package commands;

import com.minty.metrocore.MetroCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.sql.*;

public class WatchlistCommand implements CommandExecutor {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public WatchlistCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("add")) {
                File dir = plugin.getDataFolder();
                File reportsFolder = new File(dir + File.separator + "PlayerReports" + File.separator);
                File playerDb = new File(reportsFolder, "PlayerReports.db");
                String name = args[1].toString();
                String report = "";
                for (int i = 2; i < args.length; i++) {
                    report += args[i] + " ";
                }

                try {
                    String path = playerDb.getPath();
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                    statement = connection.createStatement();
                    ResultSet rsRec = statement.executeQuery("SELECT * FROM PLAYERREPORTS");
                    rsRec = statement.executeQuery("SELECT COUNT(*) AS ROWCOUNTREC FROM PLAYERREPORTS WHERE REPORTED = '" + name + "'");
                    rsRec.next();
                    int reportCountReceived = rsRec.getInt("ROWCOUNTREC");
                    rsRec.close();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("watchlist.added").replaceAll("%player%", name)));
                    Long guild = plugin.getConfig().getLong("discord.guild_id");
                    Long channelid = plugin.getConfig().getLong("discord.watchlist_channel_id");
                    java.awt.Color c = new java.awt.Color(0, 0, 255);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Watch: " + name);
                    eb.setDescription("**From:** " + sender.getName() + "\n" + "**About:** " + name + "\n" + "**Reason:** " + report + "\n" + "**Previous reports:** " + String.valueOf(reportCountReceived));
                    eb.setColor(c);

                    TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                    ch.sendMessage(eb.build()).queue();
                    return true;


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/watchlist add <username>]");
            }

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/watchlist add <username>]");
        }

        return true;
    }
}

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

public class NotesCommand implements CommandExecutor {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public NotesCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length == 0) {
            File dir = plugin.getDataFolder();
            File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
            File file = new File(Folder, "AidanNotes.db");
            String path = file.getPath();
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                statement = connection.createStatement();

                sender.sendMessage(ChatColor.GOLD + "------ Aidan's notes ------");
                ResultSet rs = statement.executeQuery("SELECT ID, NOTE, STATUS FROM AIDANNOTES WHERE STATUS = 'In progress'");
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String note = rs.getString("NOTE");
                    sender.sendMessage(ChatColor.GRAY + "#" + String.valueOf(id) + ": " + ChatColor.GREEN + note);
                }
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args[1] != null) {
                File dir = plugin.getDataFolder();
                File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
                File file = new File(Folder, "AidanNotes.db");
                String path = file.getPath();
                try {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                    statement = connection.createStatement();
                    String sql = "UPDATE AIDANNOTES SET STATUS = 'Done' WHERE ID = " + args[1].toString();
                    statement.executeUpdate(sql);
                    statement.close();
                    connection.close();

                    sender.sendMessage(ChatColor.GREEN + "Note " + ChatColor.YELLOW + "[" + args[1].toString() + "]" + ChatColor.GREEN + " removed!");
                    return true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/notes remove <id>]");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            File dir = plugin.getDataFolder();
            File Folder = new File(dir + File.separator + "AidanNotes" + File.separator);
            File file = new File(Folder, "AidanNotes.db");
            String path = file.getPath();
            if (args.length >= 2) {
                try {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                    statement = connection.createStatement();
                    ResultSet rc = statement.executeQuery("SELECT * FROM AIDANNOTES");
                    rc = statement.executeQuery("SELECT COUNT(*) FROM AIDANNOTES");
                    rc.next();
                    int RowCount = rc.getInt(1) + 1;
                    rc.close();

                    String message = "";
                    for (int i = 1; i < args.length; i++) {
                        message += args[i] + " ";
                    }

                    String message2 = message.replace("'", "");

                    String sql = "INSERT INTO AIDANNOTES (ID,NOTE,STATUS) VALUES (" + String.valueOf(RowCount) + ", '" + message2 + "', 'In progress');";
                    statement.executeUpdate(sql);
                    statement.close();
                    connection.close();
                    sender.sendMessage(ChatColor.GREEN + "Note added!");

                    Long guild = plugin.getConfig().getLong("discord.guild_id");
                    Long channelid = plugin.getConfig().getLong("discord.notes_channel_id");
                    java.awt.Color c = new java.awt.Color(255, 255, 0);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(sender.getName() + " added a new note!");
                    eb.setDescription(message);
                    eb.setColor(c);

                    TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
                    ch.sendMessage(eb.build()).queue();
                    return true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/notes add <text>]");
            }
        }
        return true;
    }
}

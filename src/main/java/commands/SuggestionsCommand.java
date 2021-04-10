package commands;

import com.minty.metrocore.MetroCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.Statement;

public class SuggestionsCommand implements CommandExecutor {

    private final MetroCore plugin;
    private static Connection connection;
    private static Statement statement;

    public SuggestionsCommand(MetroCore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (args.length > 0) {
            String report = "";
            for (int i = 0; i < args.length; i++) {
                report += args[i] + " ";
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("suggestion.added")));

            Long guild = plugin.getConfig().getLong("discord.guild_id");
            Long channelid = plugin.getConfig().getLong("discord.suggestions_channel_id");
            java.awt.Color c = new java.awt.Color(255, 0, 255);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Suggestion from: " + sender.getName());
            eb.setDescription("**Suggestion:** " + report);
            eb.setColor(c);

            TextChannel ch = plugin.jda.getGuildById(guild).getTextChannelById(channelid);
            ch.sendMessage(eb.build()).queue();

            return true;

        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "[/suggestion <text>]");
        }
        return true;
    }
}

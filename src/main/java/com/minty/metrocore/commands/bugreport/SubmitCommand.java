package com.minty.metrocore.commands.bugreport;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.BugReportsDatabase;
import com.minty.metrocore.discord.SendMessageToChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public class SubmitCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final BugReportsDatabase bugReportsDatabase;

    public SubmitCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel, BugReportsDatabase bugReportsDatabase) {
        super(command);
        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
        this.bugReportsDatabase = bugReportsDatabase;
    }

    @Override
    public String getDescription() {
        return "Submit a bug report!";
    }

    @Override
    public String getSyntax() {
        return "/bugreport submit <report>";
    }

    @Override
    public void perform(Player player, String[] args) {

        if(args.length == 0
            || args[1] == null){
            plugin.sendWithPrefix(player, "&cUsage: &e/bugreport submit <report>");
            return;
        }

        Long guildid = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.bugreport_channel_id");

        String name = player.getName();
        String report = "";
        for(int i = 1; i < args.length; i++)
            report += args[i].replace("'", "''") + " ";

        bugReportsDatabase.submitBug(name, report);
        sendMessageToChannel.SendMessage(guildid, channelid, new Color(255, 0, 0),
                "Bug Report:", "**From:** " + player.getName() + "\n**Description:**" + report);

        plugin.sendWithPrefix(player, plugin.getConfig().getString("bugreport.report_submitted"));
        for(Player p : Bukkit.getOnlinePlayers())
            if(p.hasPermission("metrocore.admin"))
                plugin.sendWithPrefix(p, plugin.getConfig().getString("bugreport.report_submitted_staff").replaceAll("%player%", player.getName()));
    }
}

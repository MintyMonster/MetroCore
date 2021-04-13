package com.minty.metrocore.commands.bugreport;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.BugReportsDatabase;
import com.minty.metrocore.discord.SendMessageToChannel;
import org.bukkit.entity.Player;

import java.awt.*;

public class FixCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final BugReportsDatabase bugReportsDatabase;



    public FixCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel, BugReportsDatabase bugReportsDatabase) {
        super(command);
        this.bugReportsDatabase = bugReportsDatabase;
        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
    }

    @Override
    public String getDescription() {
        return "Mark bugs as fixed!";
    }

    @Override
    public String getSyntax() {
        return "/bugreport fix [id]";
    }

    @Override
    public void perform(Player player, String[] args) {

        Long guildid = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.bugreport_channel_id");

        if(!player.hasPermission("metro.admin")){
            plugin.sendWithPrefix(player, "&cThis is an admin command only!");
            return;
        }

        if(args[1] == null){
            plugin.sendWithPrefix(player, "&cPlease provide an id. Usage: &e/bugreports fix [id]");
            return;
        }

        bugReportsDatabase.setFixed(String.valueOf(args[1]));
        sendMessageToChannel.SendMessage(guildid, channelid, new Color(0, 200, 255), "BugReport: #" + args[1], "Marked as **[Fixed]**");
        plugin.sendWithPrefix(player, plugin.getConfig().getString("bugreport.bugreport_fixed").replace("%id%", args[1].toString()));
    }
}

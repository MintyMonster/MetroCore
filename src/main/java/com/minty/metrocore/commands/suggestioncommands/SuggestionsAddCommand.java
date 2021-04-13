package com.minty.metrocore.commands.suggestioncommands;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.discord.SendMessageToChannel;
import org.bukkit.entity.Player;

import java.awt.*;

public class SuggestionsAddCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;

    public SuggestionsAddCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel) {
        super(command);
        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
    }

    @Override
    public String getDescription() {
        return "Add a suggestion!";
    }

    @Override
    public String getSyntax() {
        return "/suggest <suggestion>";
    }

    @Override
    public void perform(Player player, String[] args) {

        Long guild = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.suggestions_channel_id");

        if(args[0] == null){
            plugin.sendWithPrefix(player, "&cUsage: &e/suggest <suggestion>");
            return;
        }

        String suggestion = "";
        for(int i = 0; i < args.length; i++)
            suggestion += args[i] + " ";

        plugin.sendWithPrefix(player, plugin.getConfig().getString("suggestion.added"));
        sendMessageToChannel.SendMessage(guild, channelid, new Color(255, 0, 255), "Suggestion from: " + player.getName(),
                "**Suggestion: **" + suggestion);
    }
}

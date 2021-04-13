package com.minty.metrocore.discord;

import com.minty.metrocore.MetroCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class SendMessageToChannel {

    private final MetroCore plugin;

    public SendMessageToChannel(MetroCore plugin){
        this.plugin = plugin;
    }

    public void SendMessage(Long guildId, Long channelId, Color color, String title, String description){

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(color);


        TextChannel channel = plugin.jda.getGuildById(guildId).getTextChannelById(channelId);
        channel.sendMessage(eb.build()).queue();
    }
}

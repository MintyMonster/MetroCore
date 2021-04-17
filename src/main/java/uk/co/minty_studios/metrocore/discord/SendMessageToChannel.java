package uk.co.minty_studios.metrocore.discord;

import uk.co.minty_studios.metrocore.MetroCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class SendMessageToChannel {

    private final MetroCore plugin;

    public SendMessageToChannel(MetroCore plugin){
        this.plugin = plugin;
    }

    public void sendEmbed(Long guildId, Long channelId, Color color, String title, String description){

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(color);
        eb.setThumbnail("https://user-images.githubusercontent.com/78669960/114949725-7f459580-9e49-11eb-92fa-08cdb377d5e5.png");


        TextChannel channel = plugin.jda.getGuildById(guildId).getTextChannelById(channelId);
        channel.sendMessage(eb.build()).queue();
    }

    public void sendText(Long guildid, Long channelid, String description){
        TextChannel channel = plugin.jda.getGuildById(guildid).getTextChannelById(channelid);
        channel.sendMessage(description).queue();
    }

    public void sendTextCodeBlock(Long guildid, Long channelid, String description){
        TextChannel channel = plugin.jda.getGuildById(guildid).getTextChannelById(channelid);
        channel.sendMessage("```\n" + description + "\n```").queue();
    }

    public void sendError(Long guildid, Long channelid, String description){

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":x: Error:");
        eb.setDescription(description);
        eb.setColor(new Color(255, 0, 0));
        eb.setThumbnail("https://user-images.githubusercontent.com/78669960/114949725-7f459580-9e49-11eb-92fa-08cdb377d5e5.png");

        TextChannel channel = plugin.jda.getGuildById(guildid).getTextChannelById(channelid);
        channel.sendMessage(eb.build()).queue();
    }
}

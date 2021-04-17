package uk.co.minty_studios.metrocore.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.database.DiscordCodeDatabase;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import uk.co.minty_studios.metrocore.methods.GenericUseMethods;

import java.awt.*;
import java.util.UUID;

public class MessageReceivedListener extends ListenerAdapter {

    private final MetroCore plugin;
    private final DiscordLinking discordLinking;
    private final DiscordCodeDatabase discordCodeDatabase;
    private final SendMessageToChannel sendMessageToChannel;
    private final GenericUseMethods genericUseMethods;

    public MessageReceivedListener(MetroCore plugin,
                                   DiscordLinking discordLinking,
                                   DiscordCodeDatabase discordCodeDatabase,
                                   SendMessageToChannel sendMessageToChannel,
                                   GenericUseMethods genericUseMethods) {
        this.plugin = plugin;
        this.discordLinking = discordLinking;
        this.discordCodeDatabase = discordCodeDatabase;
        this.sendMessageToChannel = sendMessageToChannel;
        this.genericUseMethods = genericUseMethods;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        long guildid = plugin.getConfig().getLong("discord.guild_id");
        long channel = event.getChannel().getIdLong();
        User author = event.getAuthor();

        if(message.getContentRaw().contains("-link")) {

            if(message.getContentRaw().equalsIgnoreCase("-link")){
                sendMessageToChannel.sendTextCodeBlock(guildid, channel, "Usage: -link <code>");
                return;
            }

            String code = "";
            for(char c : message.getContentRaw().toCharArray()){
                if(Character.isDigit(c)){
                    code += c;
                }
            }

            if(discordCodeDatabase.linkExistsDiscord(author.getIdLong())){
                sendMessageToChannel.sendEmbed(guildid, channel, new Color(255, 255, 0), "Discord already linked!",
                        "[" + author.getAsMention() + "]\n\n**You've already linked your Discord! Thanks :)**\n**If this wasn't you, please contact a member of staff**");
                return;
            }

            if(discordLinking.codeExists(Integer.parseInt(code))){
                UUID uuid = discordLinking.lookForCode(Integer.parseInt(code));
                long id = author.getIdLong();

                Player player = genericUseMethods.getPlayerByUuid(uuid);
                plugin.sendWithPrefix(player, plugin.getConfig().getString("discord.link.linked"));

                sendMessageToChannel.sendEmbed(guildid, channel, new Color(0, 255, 0), "Discord linked!",
                        "[" + author.getAsMention() + "]\n\n**You've successfully linked your Discord to SurviveMetro. Thank you!**");

                event.getGuild().addRoleToMember(id, plugin.jda.getRoleById(plugin.getConfig().getLong("discord.link.role_to_give_id")));
                genericUseMethods.executeMultipleCommands(player, "discord.link.rewards.commands");
                discordCodeDatabase.linkPlayerToDiscord(uuid, id, Integer.parseInt(code));
                discordLinking.removePlayer(uuid);
                event.getMessage().delete();

            }else
                sendMessageToChannel.sendError(guildid, channel,
                        "[" + author.getAsMention() + "]\n\n**Your code doesn't exist :(\nPlease check the code, and try again!**");

        }
    }
}

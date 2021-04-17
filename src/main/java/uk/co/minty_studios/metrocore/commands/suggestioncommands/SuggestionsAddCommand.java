package uk.co.minty_studios.metrocore.commands.suggestioncommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
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
    public String getPermission(){ return "metrocore.core"; }

    @Override
    public String getDescription() {
        return "Add a suggestion!";
    }

    @Override
    public String getSyntax() {
        return "&e/suggestion submit <suggestion>";
    }

    @Override
    public void perform(Player player, String[] args) {

        Long guild = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.suggestions_channel_id");

        if(!(args.length > 1)){
            plugin.sendWithPrefix(player, "&cUsage: &e/suggestion submit <suggestion>");
            return;
        }

        String suggestion = "";
        for(int i = 1; i < args.length; i++)
            suggestion += args[i] + " ";

        plugin.sendWithPrefix(player, plugin.getConfig().getString("suggestion.added"));
        sendMessageToChannel.sendEmbed(guild, channelid, new Color(255, 0, 255), "Suggestion from: " + player.getName(),
                "**Suggestion: **" + suggestion);
    }
}

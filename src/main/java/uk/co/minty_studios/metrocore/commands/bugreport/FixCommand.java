package uk.co.minty_studios.metrocore.commands.bugreport;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.BugReportsDatabase;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
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
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Mark bugs as fixed!";
    }

    @Override
    public String getSyntax() {
        return "&e/bugreport fix [id]";
    }

    @Override
    public void perform(Player player, String[] args) {

        Long guildid = plugin.getConfig().getLong("discord.guild_id");
        Long channelid = plugin.getConfig().getLong("discord.bugreport_channel_id");

        if(!(args.length > 1)){
            plugin.sendWithPrefix(player, "&cPlease provide an id. Usage: &e/bugreports fix [id]");
            return;
        }

        bugReportsDatabase.setFixed(args[1]);
        sendMessageToChannel.sendEmbed(guildid, channelid, new Color(0, 200, 255), "BugReport: #" + args[1], "Marked as **[Fixed]** by **" + player.getName() + "**");
        plugin.sendWithPrefix(player, plugin.getConfig().getString("bugreport.bugreport_fixed").replace("%id%", args[1]));
    }
}

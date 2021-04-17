package uk.co.minty_studios.metrocore.commands.notescommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.NotesDatabase;
import uk.co.minty_studios.metrocore.discord.SendMessageToChannel;
import org.bukkit.entity.Player;

import java.awt.*;

public class NotesAddCommand extends ChildCommand {

    private final MetroCore plugin;
    private final SendMessageToChannel sendMessageToChannel;
    private final NotesDatabase notesDatabase;

    public NotesAddCommand(String command, MetroCore plugin, SendMessageToChannel sendMessageToChannel, NotesDatabase notesDatabase) {
        super(command);
        this.plugin = plugin;
        this.sendMessageToChannel = sendMessageToChannel;
        this.notesDatabase = notesDatabase;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Metro's very own StickyNotes!";
    }

    @Override
    public String getSyntax() {
        return "&e/notes add <text>";
    }

    @Override
    public void perform(Player player, String[] args) {

        long guildId = plugin.getConfig().getLong("discord.guild_id");
        long channelId = plugin.getConfig().getLong("discord.notes_channel_id");

        if(!(args.length > 1)){
            plugin.sendWithPrefix(player, "&cUsage: &e/notes add <text>");
            return;
        }

        String message = "";
        for(int i = 1; i < args.length; i++)
            message += args[i].replace("'", "''") + " ";

        notesDatabase.addNote(message);
        sendMessageToChannel.sendEmbed(guildId, channelId, new Color(255, 255, 0),
                player.getName() + " added a new note!", message);

        plugin.sendWithPrefix(player, "&aNote added, thanks!");

    }
}

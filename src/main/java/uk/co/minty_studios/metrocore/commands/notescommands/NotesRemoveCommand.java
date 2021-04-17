package uk.co.minty_studios.metrocore.commands.notescommands;

import uk.co.minty_studios.metrocore.MetroCore;
import uk.co.minty_studios.metrocore.commands.ChildCommand;
import uk.co.minty_studios.metrocore.database.NotesDatabase;
import org.bukkit.entity.Player;

public class NotesRemoveCommand extends ChildCommand {

    private final MetroCore plugin;
    private final NotesDatabase notesDatabase;
    
    public NotesRemoveCommand(String command, MetroCore plugin, NotesDatabase notesDatabase) {
        super(command);
        this.plugin = plugin;
        this.notesDatabase = notesDatabase;
    }

    @Override
    public String getPermission(){ return "metrocore.admin"; }

    @Override
    public String getDescription() {
        return "Remove a note";
    }

    @Override
    public String getSyntax() {
        return "&e/notes remove <id>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(!(args.length > 1)){
            plugin.sendWithPrefix(player, "&cUsage: &e/notes remove <id>");
            return;
        }

        notesDatabase.removeNote(args[1]);
        plugin.sendWithPrefix(player, "&aNote removed.");
    }
}

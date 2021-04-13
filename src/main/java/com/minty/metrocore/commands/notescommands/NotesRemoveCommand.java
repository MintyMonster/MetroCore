package com.minty.metrocore.commands.notescommands;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.NotesDatabase;
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
    public String getDescription() {
        return "Remove a note";
    }

    @Override
    public String getSyntax() {
        return "/notes remove <id>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args[1] == null){
            plugin.sendWithPrefix(player, "&cUsage: &e/notes remove <id>");
            return;
        }

        notesDatabase.removeNote(args[1]);
        plugin.sendWithPrefix(player, "&aNote removed.");
    }
}

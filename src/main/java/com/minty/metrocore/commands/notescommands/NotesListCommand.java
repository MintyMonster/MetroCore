package com.minty.metrocore.commands.notescommands;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.NotesDatabase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NotesListCommand extends ChildCommand {

    private final MetroCore plugin;
    private final NotesDatabase notesDatabase;

    public NotesListCommand(String command, MetroCore plugin, NotesDatabase notesDatabase) {
        super(command);
        this.plugin = plugin;
        this.notesDatabase = notesDatabase;
    }

    @Override
    public String getDescription() {
        return "List all current notes";
    }

    @Override
    public String getSyntax() {
        return "/notes list";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args[0] == null){
            plugin.sendWithPrefix(player, "&cUsage: &e/notes list");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "------ Aidan's notes ------");
        notesDatabase.listNotes(player);
    }
}

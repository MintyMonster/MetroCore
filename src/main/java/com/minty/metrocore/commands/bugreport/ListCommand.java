package com.minty.metrocore.commands.bugreport;

import com.minty.metrocore.MetroCore;
import com.minty.metrocore.commands.ChildCommand;
import com.minty.metrocore.database.BugReportsDatabase;
import org.bukkit.entity.Player;

public class ListCommand extends ChildCommand {

    private final MetroCore plugin;
    private final BugReportsDatabase bugReportsDatabase;

    public ListCommand(String command, MetroCore plugin, BugReportsDatabase bugReportsDatabase) {
        super(command);
        this.bugReportsDatabase = bugReportsDatabase;
        this.plugin = plugin;
    }

    @Override
    public String getDescription() {
        return "List all bug reports";
    }

    @Override
    public String getSyntax() {
        return "/bugreport list";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(!player.hasPermission("metro.admin")){
            plugin.sendWithPrefix(player, "&cThis is an admin command only!");
            return;
        }

        bugReportsDatabase.listBugs(player);
    }
}
